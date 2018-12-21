package priv.dengjl.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import priv.dengjl.bean.RedPacketBean;

@Repository
public interface RedPacketBeanMapper {
	// 通过【id】获取红包
	RedPacketBean getRedPacket(Integer id);
	// 数据库加锁
	RedPacketBean getRedPacket2(Integer id);
	// 扣减红包剩余数量
	int deductRedPacketAmount(Integer id);
	// 乐观锁，version
	int deductRedPacketAmountByVersion(@Param("id") Integer redPacketId, @Param("version") Integer version);
	// 初始化可用于抢的红包
	List<RedPacketBean> getAvailableRedPackets();
	// 扣减红包剩余数量
	int deductRedPacketByAmount(@Param("id") Integer id, @Param("amount") Integer amount);
}