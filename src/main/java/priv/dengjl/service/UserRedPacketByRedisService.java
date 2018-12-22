package priv.dengjl.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import priv.dengjl.bean.UserRedPacketBean;
import priv.dengjl.constant.RedisConstant;
import priv.dengjl.dao.RedPacketBeanMapper;
import redis.clients.jedis.Jedis;

@Service
public class UserRedPacketByRedisService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserRedPacketByRedisService.class);
	
	private static final int BATH_SIZE = 1000;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Autowired
	private RedPacketBeanMapper redPacketBeanMapper;
	
	public void saveUserRedPacketByRedis(Integer redPacketId, Double unitAmount) {
		// 开启多线程，不然该用户体检很差，异步回写
		taskExecutor.execute(()->{
			long start = System.currentTimeMillis();
			logger.debug("{}", "开始保持数据");
			BoundListOperations boundListOps = redisTemplate.boundListOps(RedisConstant.PRE + redPacketId);
			Long size = boundListOps.size();
			
			long times = (size % BATH_SIZE == 0) ? (size / BATH_SIZE) : (size / BATH_SIZE) + 1;
			
			int totalCount = 0;
			List<UserRedPacketBean> userRedPacketList = new ArrayList<>(BATH_SIZE);
			for (int i = 0; i < times; i++) {
				userRedPacketList.clear();
				List range = boundListOps.range(i * BATH_SIZE, (i + 1) * BATH_SIZE - 1);
				for (Object object : range) {
					String args = object.toString();
					String[] attr = args.split("-");
					int userId = Integer.parseInt(attr[0]);
					long time = Long.parseLong(attr[1]);
					
					UserRedPacketBean bean = new UserRedPacketBean();
					bean.setRedPacketId(redPacketId);
					bean.setUserId(userId);
					bean.setAmount(new BigDecimal(unitAmount));
					bean.setGetTime(new Timestamp(time));
					bean.setNote(String.format("用户【%s】抢到红包【%s】", userId, redPacketId));
					userRedPacketList.add(bean);
				}
				
				totalCount += executeBath(userRedPacketList);
			}
			
			// 扣减库存
			redPacketBeanMapper.deductRedPacketByAmount(redPacketId, totalCount);
			
			logger.debug("共{}条数据被保存", totalCount);
			long end = System.currentTimeMillis();
			logger.info("保存数据共消耗：{}毫秒", (end - start));
		});
	}

	private int executeBath(List<UserRedPacketBean> userRedPacketList) {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			// 关闭自动提交
			connection.setAutoCommit(false);
			String sql = "INSERT INTO USER_RED_PACKET(RED_PACKET_ID, USER_ID, AMOUNT, GET_TIME, NOTE) VALUES(?, ?, ?, ?, ?)";
			PreparedStatement pst = connection.prepareStatement(sql);
			for (UserRedPacketBean userRedPacketBean : userRedPacketList) {
				pst.setInt(1, userRedPacketBean.getRedPacketId());
				pst.setInt(2, userRedPacketBean.getUserId());
				pst.setBigDecimal(3, userRedPacketBean.getAmount());
				pst.setTimestamp(4, new Timestamp(userRedPacketBean.getGetTime().getTime()));
				pst.setString(5, userRedPacketBean.getNote());
				pst.addBatch();
			}
			int length = pst.executeBatch().length;
			
			// 
			connection.commit();
			return length;
		} catch (SQLException e) {
			throw new RuntimeException("抢红包发生内部异常：" + e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	public int insertRedPacketByRedis(Integer redPacketId, Integer userId) {
		long currentTime = System.currentTimeMillis();
		
		Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
		
		// 从redis中取出项目初始化加载到redis中的lua脚本返回的sha
		String sha1 = jedis.get(RedisConstant.RED_PACKET_SCRIPT_SHA_KEY);
		// 执行lua脚本
		
		Integer result = ((Long) jedis.evalsha(sha1, 1, redPacketId + "", userId + "-" + currentTime)).intValue();
		if (result == 2) {
			long start = System.currentTimeMillis();
			// 获取单个小红包金额
			String unitAmountStr = jedis.hget(RedisConstant.RED_PRE + redPacketId, RedisConstant.UNIT_AMOUNT_KEY);
			double unitAmount = Double.parseDouble(unitAmountStr);
			// 异步将redis数据持久化到数据库
			saveUserRedPacketByRedis(redPacketId, unitAmount);
			
			long end = System.currentTimeMillis();
			logger.info("最后一个红包消耗：{}毫秒", (end - start));
		}
		
		// 归还资源
		jedis.close();
		long currentTimeEnd = System.currentTimeMillis();
		logger.info("当前红包为：{}, 用户为：{}， 消耗：{}毫秒", redPacketId, userId, (currentTimeEnd - currentTime));
		
		return result;
	}
	
}
