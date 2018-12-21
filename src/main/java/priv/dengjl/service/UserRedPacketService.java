package priv.dengjl.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import priv.dengjl.bean.RedPacketBean;
import priv.dengjl.bean.UserRedPacketBean;
import priv.dengjl.dao.RedPacketBeanMapper;
import priv.dengjl.dao.UserRedPacketBeanMapper;

@Service
public class UserRedPacketService {
	@Autowired
	private UserRedPacketBeanMapper userRedPacketBeanMapper;
	
	@Autowired
	private RedPacketBeanMapper redPacketBeanMapper;
	
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int insertRedPacket(Integer redPacketId, Integer userId) {
		RedPacketBean redPacket = redPacketBeanMapper.getRedPacket(redPacketId);
		// 库存判断
		Integer stock = redPacket.getStock();
		if (stock > 0) {
			// 扣减库存
			redPacketBeanMapper.deductRedPacketAmount(redPacketId);
			// 插入红包记录
			UserRedPacketBean bean = new UserRedPacketBean();
			bean.setRedPacketId(redPacketId);
			bean.setUserId(userId);
			bean.setAmount(new BigDecimal(redPacket.getUnitAmount()));
			bean.setNote(String.format("用户【%s】抢到红包【%s】", userId, redPacketId));
			return userRedPacketBeanMapper.insertRedPacket(bean);
		} else {
			return 0;
		}
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int insertRedPacket2(Integer redPacketId, Integer userId) {
		// 强一致加锁，解决超发数量问题
		RedPacketBean redPacket = redPacketBeanMapper.getRedPacket2(redPacketId);
		// 库存判断
		Integer stock = redPacket.getStock();
		if (stock > 0) {
			// 扣减库存
			redPacketBeanMapper.deductRedPacketAmount(redPacketId);
			// 插入红包记录
			UserRedPacketBean bean = new UserRedPacketBean();
			bean.setRedPacketId(redPacketId);
			bean.setUserId(userId);
			bean.setAmount(new BigDecimal(redPacket.getUnitAmount()));
			bean.setNote(String.format("用户【%s】抢到红包【%s】", userId, redPacketId));
			return userRedPacketBeanMapper.insertRedPacket(bean);
		} else {
			return 0;
		}
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public synchronized int insertRedPacket3(Integer redPacketId, Integer userId) {
		RedPacketBean redPacket = redPacketBeanMapper.getRedPacket(redPacketId);
		// 库存判断
		Integer stock = redPacket.getStock();
		if (stock > 0) {
			// 扣减库存
			redPacketBeanMapper.deductRedPacketAmount(redPacketId);
			// 插入红包记录
			UserRedPacketBean bean = new UserRedPacketBean();
			bean.setRedPacketId(redPacketId);
			bean.setUserId(userId);
			bean.setAmount(new BigDecimal(redPacket.getUnitAmount()));
			bean.setNote(String.format("用户【%s】抢到红包【%s】", userId, redPacketId));
			return userRedPacketBeanMapper.insertRedPacket(bean);
		} else {
			return 0;
		}
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int insertRedPacket4(Integer redPacketId, Integer userId) {
		RedPacketBean redPacket = redPacketBeanMapper.getRedPacket(redPacketId);
		// 库存判断
		Integer stock = redPacket.getStock();
		if (stock > 0) {
			// 扣减库存
			int result = redPacketBeanMapper.deductRedPacketAmountByVersion(redPacketId, redPacket.getVsersion());
			if (result == 0) {
				return 0;
			}
			
			// 插入红包记录
			UserRedPacketBean bean = new UserRedPacketBean();
			bean.setRedPacketId(redPacketId);
			bean.setUserId(userId);
			bean.setAmount(new BigDecimal(redPacket.getUnitAmount()));
			bean.setNote(String.format("用户【%s】抢到红包【%s】", userId, redPacketId));
			return userRedPacketBeanMapper.insertRedPacket(bean);
		} else {
			return 0;
		}
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int insertRedPacket5(Integer redPacketId, Integer userId) {
		for (int i = 0; i < 4; i++) {
			RedPacketBean redPacket = redPacketBeanMapper.getRedPacket(redPacketId);
			// 库存判断
			Integer stock = redPacket.getStock();
			if (stock > 0) {
				// 扣减库存
				int result = redPacketBeanMapper.deductRedPacketAmountByVersion(redPacketId, redPacket.getVsersion());
				if (result == 0) {
					// 重新抢，四次机会
					continue;
				}
				
				// 插入红包记录
				UserRedPacketBean bean = new UserRedPacketBean();
				bean.setRedPacketId(redPacketId);
				bean.setUserId(userId);
				bean.setAmount(new BigDecimal(redPacket.getUnitAmount()));
				bean.setNote(String.format("用户【%s】抢到红包【%s】", userId, redPacketId));
				return userRedPacketBeanMapper.insertRedPacket(bean);
			} else {
				return 0;
			}
		}
		return 0;
	}
}
