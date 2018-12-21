import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import priv.dengjl.bean.RedPacketBean;
import priv.dengjl.service.RedPacketService;

public class TestService {
	
	private static final Logger logger = LoggerFactory.getLogger(TestService.class);

	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-mybaties.xml");
		RedPacketService service = context.getBean(RedPacketService.class);
		RedPacketBean redPacket = service.getRedPacket(1);
		logger.debug("{}", redPacket);
	}
	
}
