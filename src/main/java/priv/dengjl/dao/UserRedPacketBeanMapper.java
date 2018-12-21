package priv.dengjl.dao;

import org.springframework.stereotype.Repository;

import priv.dengjl.bean.UserRedPacketBean;

@Repository
public interface UserRedPacketBeanMapper {
	int insertRedPacket(UserRedPacketBean userRedPacketBean);
}