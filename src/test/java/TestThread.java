import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import priv.dengjl.bean.RedPacketBean;
import task.MessagePrinterTask;

public class TestThread {
	
	private static final Logger logger = LoggerFactory.getLogger(TestThread.class);

	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-mybaties.xml");
		ThreadPoolTaskExecutor service = context.getBean(ThreadPoolTaskExecutor.class);
		service.execute(new MessagePrinterTask(40, "hello，你好"));
		logger.debug("{}", service);
	}
	
}
