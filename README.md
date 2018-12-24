# red-packet-redis（秒杀系统）
业务基于抢红包，技术采用 `ssm架构` + `redis` + mysql 开发

## 总体架构图

![Architecture](https://raw.githubusercontent.com/dengjili/red-packet-redis/master/picture/Architecture.jpg)

## 快速了解

### 系统配置
![config](https://raw.githubusercontent.com/dengjili/red-packet-redis/master/picture/config.png)

### 配置系统启动类RedisRedPacketetStartupListener
```java
@Configuration
public class RedisRedPacketetStartupListener implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(RedisRedPacketetStartupListener.class);
	
	@Autowired
	private RedPacketService redPacketService;
	
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		try {
			Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
			// 删除旧数据
			jedis.flushAll();
			logger.info("删除旧数据完成");
			
			// 读取lua脚本文件
			String sha = loadLuaScript(jedis);
			// 设置lua脚本的sha值
			jedis.set(RedisConstant.RED_PACKET_SCRIPT_SHA_KEY, sha);
			// 关闭一下资源
			jedis.close();
			logger.info("初始化redis中的lua脚本完成");
			
			// 读取数据库配置数据
			List<RedPacketBean> redPackets = redPacketService.getAvailableRedPackets();
			logger.info("读取数据库配置数据完成,数据条数：{}", redPackets.size());
			
			// 将数据库数据配置到redis中
			SessionCallback callback = operations -> {
				for (RedPacketBean redPacketBean : redPackets) {
					Integer redPacketId = redPacketBean.getId();
					String stock = redPacketBean.getStock() + "";
					String unitAmount = redPacketBean.getUnitAmount() + "";
					// 以单个红包单独存一个关键字
					String key = RedisConstant.RED_PRE + redPacketId;
					operations.boundHashOps(key).put(RedisConstant.STOCK_KEY, stock);
					operations.boundHashOps(key).put(RedisConstant.UNIT_AMOUNT_KEY, unitAmount);
				}
				return null;
			};
			redisTemplate.executePipelined(callback);
			logger.info("将数据库数据配置到redis中完成");
			
			// 归还资源
			jedis.close();
		} catch (Exception e) {
			logger.error("RedisRedPacketetStartupListener初始化失败，请核查。{}", e.getMessage());
		}
	}

	// 返回lua脚本返回的sha值，设置在redis中
	private String loadLuaScript(Jedis jedis) throws IOException {
		// 组件流
		InputStream inputStream = RedisRedPacketetStartupListener.class.getClassLoader().getResourceAsStream(RedisConstant.RED_PACKET_SCRIPT_FILENAME);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(inputStream.available());
		// 包装流
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		
		byte[] buffer = new byte[1024]; 
		int len = 0;
		while ((len = bis.read(buffer)) > 0) {
			bos.write(buffer, 0, len);
			// 如果不调用该方法，数据都保持在缓存区,最终baos为空数据
			bos.flush();
		}
		
		byte[] sha1 = jedis.scriptLoad(baos.toByteArray());
		return new String(sha1);
	}

}

```

### RED_PACKET_SEC_KILL.LUA【lua脚本】 
```lua
--当前被抢红包 key
local redPacket = 'red_packet_'..KEYS[1] 

--获取当前红包库存
local stock = tonumber(redis.call('hget', redPacket, 'stock')) 
--没有库存，返回为 0 
if (stock <= 0) 
then 
	return 0 
end 

--缓存抢红包列表信息列表 key
local listKey = 'red_packet_list_'..KEYS[1]  

--库存减 1
stock = stock-1
--保存当前库存
redis.call('hset', redPacket, 'stock', tostring(stock)) 
--往链表中加入当前红包信息
redis.call('lpush', listKey, ARGV[1])  
--如果是最后一个红包，则返回 2 ，表示抢红包已经结束，需要将列表中的数据保存到数据库中
if (stock == 0) 
then 
	return 2 
end  
--如果并非最后一个红包，则返回 l ，表示抢红包成功
return 1
```

### 服务端核心处理
```java
public int insertRedPacketByRedis(Integer redPacketId, Integer userId) {
		long currentTime = System.currentTimeMillis();
		
		Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
		
		// 从redis中取出项目初始化加载到redis中的lua脚本返回的sha
		String sha1 = jedis.get(RedisConstant.RED_PACKET_SCRIPT_SHA_KEY);
		// 执行lua脚本
		
		Integer result = ((Long) jedis.evalsha(sha1, 1, redPacketId + "", userId + "-" + currentTime)).intValue();
		if (result == 2) {
			long start = System.currentTimeMillis();
			// 获取单个小红包金额
			String unitAmountStr = jedis.hget(RedisConstant.RED_PRE + redPacketId, RedisConstant.UNIT_AMOUNT_KEY);
			double unitAmount = Double.parseDouble(unitAmountStr);
			// 异步将redis数据持久化到数据库
			saveUserRedPacketByRedis(redPacketId, unitAmount);
			
			long end = System.currentTimeMillis();
			logger.info("最后一个红包消耗：{}毫秒", (end - start));
		}
		
		// 归还资源
		jedis.close();
		long currentTimeEnd = System.currentTimeMillis();
		logger.info("当前红包为：{}, 用户为：{}， 消耗：{}毫秒", redPacketId, userId, (currentTimeEnd - currentTime));
		
		return result;
	}
```

### 异步将数据持久化到数据库
```java
public void saveUserRedPacketByRedis(Integer redPacketId, Double unitAmount) {
		// 开启多线程，不然该用户体检很差，异步回写
		taskExecutor.execute(()->{
			long start = System.currentTimeMillis();
			logger.debug("{}", "开始保持数据");
			BoundListOperations boundListOps = redisTemplate.boundListOps(RedisConstant.PRE + redPacketId);
			Long size = boundListOps.size();
			
			long times = (size % BATH_SIZE == 0) ? (size / BATH_SIZE) : (size / BATH_SIZE) + 1;
			
			int totalCount = 0;
			List<UserRedPacketBean> userRedPacketList = new ArrayList<>(BATH_SIZE);
			for (int i = 0; i < times; i++) {
				userRedPacketList.clear();
				List range = boundListOps.range(i * BATH_SIZE, (i + 1) * BATH_SIZE - 1);
				for (Object object : range) {
					String args = object.toString();
					String[] attr = args.split("-");
					int userId = Integer.parseInt(attr[0]);
					long time = Long.parseLong(attr[1]);
					
					UserRedPacketBean bean = new UserRedPacketBean();
					bean.setRedPacketId(redPacketId);
					bean.setUserId(userId);
					bean.setAmount(new BigDecimal(unitAmount));
					bean.setGetTime(new Timestamp(time));
					bean.setNote(String.format("用户【%s】抢到红包【%s】", userId, redPacketId));
					userRedPacketList.add(bean);
				}
				
				totalCount += executeBath(userRedPacketList);
			}
			
			// 扣减库存
			redPacketBeanMapper.deductRedPacketByAmount(redPacketId, totalCount);
			
			logger.debug("共{}条数据被保存", totalCount);
			long end = System.currentTimeMillis();
			logger.info("保存数据共消耗：{}毫秒", (end - start));
		});
	}
```



