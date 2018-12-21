package priv.dengjl.constant;

public interface RedisConstant {
	/** 执行redis lua脚本名后返回 sha值*/
	String RED_PACKET_SCRIPT_SHA_KEY = "RED_PACKET_SCRIPT_SHA_KEY";
	/** redis lua脚本名*/
	String RED_PACKET_SCRIPT_FILENAME = "RED_PACKET_SEC_KILL.LUA";
	/** */
	String UNIT_AMOUNT_KEY = "unit_amount";
	/** */
	String STOCK_KEY = "stock";
	
	String PRE = "red_packet_list_";
	
	String RED_PRE = "red_packet_";
}
