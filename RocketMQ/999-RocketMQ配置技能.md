# RocketMQ本地集群配置

**[更多完整的配置参数参考](9999-RocketMQ配置参数大全-持续收录.md)**

## 修改NameServer默认端口达到本地集群配置

conf目录下新建一个配置文件conf/namesrv.properties，文件内容为：
```properties
listenPort=2058
```



使用 ```mqnamesrv -c ../conf/namesrv.properties```  指定nameserver配置文件，示例：

```
PS E:\111softwares\rocketmq-all-4.4.0-bin-release\bin> .\mqnamesrv -c ../conf/namesrv.properties
"set java env ok."
Java HotSpot(TM) 64-Bit Server VM warning: Using the DefNew young collector with the CMS collector is deprecated and will likely be removed in a future release
Java HotSpot(TM) 64-Bit Server VM warning: UseCMSCompactAtFullCollection is deprecated and will likely be removed in a future release.
load config properties file OK, ../conf/namesrv.properties
The Name Server boot success. serializeType=JSON
```


## 配置Broker本地集群

```
./mqbroker -n localhost:9876 -c ../conf/2m-2s-sync/broker-a.properties
```

在conf/broker-*.properties配置文件中指定nameserver：

```properties
brokerClusterName = DefaultCluster
brokerName = broker-a
brokerId = 0
deleteWhen = 04
fileReservedTime = 48
brokerRole = ASYNC_MASTER
flushDiskType = ASYNC_FLUSH
autoCreateTopicEnable=true
namesrvAddr=localhost:9876;localhost:2058
listenPort=10911
```



## 2主2从同步集群配置

2m-2s-sync目录下：

### 第一个主从节点

#### broker-a.properties

```properties
brokerClusterName=DefaultCluster
brokerName=broker-a
brokerId=0
deleteWhen=04
fileReservedTime=48
brokerRole=SYNC_MASTER
flushDiskType=ASYNC_FLUSH
namesrvAddr=localhost:9876;localhost:2058
listenPort=10911
# 是否允许Broker 自动创建Topic，建议线下开启，线上关闭
autoCreateTopicEnable=true
#存储路径
storePathRootDir=/usr/local/rocketmq/store
#commitLog存储路径
storePathCommitLog=/usr/local/rocketmq/store/commitlog
#消费队列存储路径
storePathConsumeQueue=/usr/local/rocketmq/store/consumequeue
# 消息索引存储路径
storePathIndex=/usr/local/rocketmq/store/index
# checkpoint 文件存储路径
storeCheckpoint=/usr/local/rocketmq/store/checkpoint
#abort 文件存储路径
abortFile=/usr/local/rocketmq/store/abort
```

#### broker-a-s.properties

```properties
brokerClusterName=DefaultCluster
brokerName=broker-a
brokerId=1
deleteWhen=04
fileReservedTime=48
brokerRole=SLAVE
flushDiskType=ASYNC_FLUSH
namesrvAddr=localhost:9876;localhost:2058
listenPort=20911
# 是否允许Broker 自动创建Topic，建议线下开启，线上关闭
autoCreateTopicEnable=true
#存储路径
storePathRootDir=/usr/local/rocketmq/as/store
#commitLog存储路径
storePathCommitLog=/usr/local/rocketmq/as/store/commitlog
#消费队列存储路径
storePathConsumeQueue=/usr/local/rocketmq/as/store/consumequeue
# 消息索引存储路径
storePathIndex=/usr/local/rocketmq/as/store/index
# checkpoint 文件存储路径
storeCheckpoint=/usr/local/rocketmq/as/store/checkpoint
#abort 文件存储路径
abortFile=/usr/local/rocketmq/as/store/abort
```


#### broker-b.properties

```properties
brokerClusterName=DefaultCluster
brokerName=broker-b
brokerId=0
deleteWhen=04
fileReservedTime=48
brokerRole=SYNC_MASTER
flushDiskType=ASYNC_FLUSH
namesrvAddr=localhost:9876;localhost:2058
listenPort=30911
# 是否允许Broker 自动创建Topic，建议线下开启，线上关闭
autoCreateTopicEnable=true
#存储路径
storePathRootDir=/usr/local/rocketmq/bm/store
#commitLog存储路径
storePathCommitLog=/usr/local/rocketmq/bm/store/commitlog
#消费队列存储路径
storePathConsumeQueue=/usr/local/rocketmq/bm/store/consumequeue
# 消息索引存储路径
storePathIndex=/usr/local/rocketmq/bm/store/index
# checkpoint 文件存储路径
storeCheckpoint=/usr/local/rocketmq/bm/store/checkpoint
#abort 文件存储路径
abortFile=/usr/local/rocketmq/bm/store/abort
```

#### broker-b-s.properties

```properties
brokerClusterName=DefaultCluster
brokerName=broker-b
brokerId=1
deleteWhen=04
fileReservedTime=48
brokerRole=SLAVE
flushDiskType=ASYNC_FLUSH
namesrvAddr=localhost:9876;localhost:2058
listenPort=40911
# 是否允许Broker 自动创建Topic，建议线下开启，线上关闭
autoCreateTopicEnable=true
#存储路径
storePathRootDir=/usr/local/rocketmq/bs/store
#commitLog存储路径
storePathCommitLog=/usr/local/rocketmq/bs/store/commitlog
#消费队列存储路径
storePathConsumeQueue=/usr/local/rocketmq/bs/store/consumequeue
# 消息索引存储路径
storePathIndex=/usr/local/rocketmq/bs/store/index
# checkpoint 文件存储路径
storeCheckpoint=/usr/local/rocketmq/bs/store/checkpoint
#abort 文件存储路径
abortFile=/usr/local/rocketmq/bs/store/abort
```

在bin目录下分别执行，依次启动1主2从集群的Broker：

```
./mqbroker -n localhost:9876;localhost:2058 -c ../conf/2m-2s-sync/broker-a.properties
```


```
./mqbroker -n localhost:9876;localhost:2058 -c ../conf/2m-2s-sync/broker-a-s.properties
```


```
./mqbroker -n localhost:9876;localhost:2058 -c ../conf/2m-2s-sync/broker-b.properties
```


```
./mqbroker -n localhost:9876;localhost:2058 -c ../conf/2m-2s-sync/broker-b-s.properties
```

