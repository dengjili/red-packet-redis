import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

public class TestRedis {
	
	private static final Logger logger = LoggerFactory.getLogger(TestRedis.class);

	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-mybaties.xml");
		RedisTemplate redisTemplate = context.getBean(RedisTemplate.class);
		logger.debug("{}", redisTemplate);
	}
}
