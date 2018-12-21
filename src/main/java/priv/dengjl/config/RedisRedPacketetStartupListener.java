package priv.dengjl.config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import priv.dengjl.bean.RedPacketBean;
import priv.dengjl.constant.RedisConstant;
import priv.dengjl.service.RedPacketService;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisRedPacketetStartupListener implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(RedisRedPacketetStartupListener.class);
	
	@Autowired
	private RedPacketService redPacketService;
	
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		try {
			Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
			// 读取lua脚本文件
			String sha = loadLuaScript(jedis);
			// 设置lua脚本的sha值
			jedis.set(RedisConstant.RED_PACKET_SCRIPT_SHA_KEY, sha);
			// 关闭一下资源
			jedis.close();
			logger.info("初始化redis中的lua脚本完成");
			
			// 读取数据库配置数据
			List<RedPacketBean> redPackets = redPacketService.getAvailableRedPackets();
			logger.info("读取数据库配置数据完成,数据条数：{}", redPackets.size());
			
			// 将数据库数据配置到redis中
			SessionCallback callback = operations -> {
				for (RedPacketBean redPacketBean : redPackets) {
					Integer redPacketId = redPacketBean.getId();
					String stock = redPacketBean.getStock() + "";
					String unitAmount = redPacketBean.getUnitAmount() + "";
					// 以单个红包单独存一个关键字
					String key = RedisConstant.RED_PRE + redPacketId;
					operations.boundHashOps(key).put(RedisConstant.STOCK_KEY, stock);
					operations.boundHashOps(key).put(RedisConstant.UNIT_AMOUNT_KEY, unitAmount);
				}
				return null;
			};
			redisTemplate.executePipelined(callback);
			logger.info("将数据库数据配置到redis中完成");
			
			
		} catch (Exception e) {
			logger.error("RedisRedPacketetStartupListener初始化失败，请核查。{}", e.getMessage());
		}
	}

	// 返回lua脚本返回的sha值，设置在redis中
	private String loadLuaScript(Jedis jedis) throws IOException {
		// 组件流
		InputStream inputStream = RedisRedPacketetStartupListener.class.getClassLoader().getResourceAsStream(RedisConstant.RED_PACKET_SCRIPT_FILENAME);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(inputStream.available());
		// 包装流
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		
		byte[] buffer = new byte[1024]; 
		int len = 0;
		while ((len = bis.read(buffer)) > 0) {
			bos.write(buffer, 0, len);
			// 如果不调用该方法，数据都保持在缓存区,最终baos为空数据
			bos.flush();
		}
		
		byte[] sha1 = jedis.scriptLoad(baos.toByteArray());
		return new String(sha1);
	}

}
