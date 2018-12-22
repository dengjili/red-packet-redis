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