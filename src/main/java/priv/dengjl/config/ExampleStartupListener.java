package priv.dengjl.config;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.ServletContextAware;

@Configuration
public class ExampleStartupListener implements ApplicationContextAware, ServletContextAware, InitializingBean,
		ApplicationListener<ContextRefreshedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(ExampleStartupListener.class);

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		logger.info("1 => StartupListener.setApplicationContext");
	}

	@Override
	public void setServletContext(ServletContext context) {
		logger.info("2 => StartupListener.setServletContext");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("3 => StartupListener.afterPropertiesSet");
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent evt) {
		logger.info("4.1 => MyApplicationListener.onApplicationEvent");
		if (evt.getApplicationContext().getParent() == null) {
			logger.info("4.2 => MyApplicationListener.onApplicationEvent");
		}
	}
}
