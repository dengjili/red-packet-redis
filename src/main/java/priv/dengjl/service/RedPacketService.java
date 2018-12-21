package priv.dengjl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import priv.dengjl.bean.RedPacketBean;
import priv.dengjl.dao.RedPacketBeanMapper;

@Service
public class RedPacketService {
	@Autowired
	private RedPacketBeanMapper dao;
	
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RedPacketBean getRedPacket(Integer id) {
		return dao.getRedPacket(id);
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int deductRedPacketAmount(Integer id) {
		return dao.deductRedPacketAmount(id);
	}
	

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<RedPacketBean> getAvailableRedPackets() {
		return dao.getAvailableRedPackets();
	}
}
