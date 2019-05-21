# Redis内存淘汰策略


## Redis淘汰策略

当达到最大内存```maxmemory```限制时，使用```maxmemory-policy```配置指令配置Redis的确切行为。
配置方式： maxmemory-policy options，默认为

- **noeviction**： 不会移除任何key，在写的时候直接返回error
- **volatile-lru** ： 使用LRU算法清除过期的key
- **allkeys-lru** ： 使用LRU算法清除key
- **volatile-lfu** ： 使用LFU算法清除过期的key
- **allkeys-lfu** ： 使用LFU算法清除key
- **volatile-random** ： 在过期的key中随机移除一个key
- **allkeys-random** ： 在所有key中随机移除一个key
- **volatile-ttl** ： 移除具有具有最近过期时间的key


- [Redis-缓存](https://redis.io/topics/lru-cache)