package task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessagePrinterTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(MessagePrinterTask.class);

	private int count;
	
	private String message;
	
	public MessagePrinterTask(int count, String message) {
		super();
		this.count = count;
		this.message = message;
	}

	@Override
	public void run() {
		for (int i = 0; i < count; i++) {
			logger.debug("当前第{}次输出：{}", i, message);
		}
	}

}
