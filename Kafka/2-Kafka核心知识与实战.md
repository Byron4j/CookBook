# Kafka核心知识与实战

## 1. Kafka概述

### 1.1 核心概念

```
Kafka核心组件:

Producer（生产者）
    │
    ▼ 发送消息
┌─────────────┐
│   Broker    │ ← Kafka服务器，负责存储和转发
│  (集群)     │
└──────┬──────┘
       │ 订阅消费
       ▼
Consumer（消费者）

关键术语:
Topic:    消息主题，逻辑分类
Partition: 分区，Topic的物理分片，实现并行处理
Offset:   消息在分区中的唯一标识（位移）
Replica:  副本，保证高可用
ISR:      In-Sync Replicas，同步副本集合

Topic分区示例:

Topic: order-events
├── Partition 0 → [msg-0, msg-3, msg-6...] → Replica(Leader: B1, Follower: B2)
├── Partition 1 → [msg-1, msg-4, msg-7...] → Replica(Leader: B2, Follower: B3)
└── Partition 2 → [msg-2, msg-5, msg-8...] → Replica(Leader: B3, Follower: B1)

生产者发送: 默认轮询(round-robin)分区
           或按key哈希选择分区
           
消费者组: Group ID = order-group
Consumer 0 → 消费 Partition 0, 1
Consumer 1 → 消费 Partition 2
（分区只能被组内一个消费者消费）
```

### 1.2 与RocketMQ对比

| 特性 | Kafka | RocketMQ |
|------|-------|----------|
| 开发方 | Apache / LinkedIn | Apache / 阿里 |
| 设计目标 | 高吞吐日志流 | 金融级可靠性 |
| 单机吞吐 | 百万级TPS | 十万级TPS |
| 延迟 | ms级 | ms级 |
| 消息可靠性 | 高（可配置） | 极高（金融级） |
| 事务消息 | 支持（幂等+事务） | 原生支持 |
| 延迟消息 | 不支持（需插件） | 原生支持18级 |
| 消息回溯 | 支持（按offset） | 支持（按时间/offset） |
| 适用场景 | 日志采集、大数据 | 电商交易、金融 |

## 2. Kafka架构

### 2.1 生产者设计

```
生产者发送流程:

ProducerRecord
    │
    ▼
┌─────────────┐
│ 拦截器链     │ ← 自定义预处理
└──────┬──────┘
       ▼
┌─────────────┐
│ 序列化器     │ ← StringSerializer / JsonSerializer
└──────┬──────┘
       ▼
┌─────────────┐
│ 分区器       │ ← DefaultPartitioner（key哈希/轮询）
└──────┬──────┘
       ▼
┌─────────────┐
│ 消息累加器   │ ← RecordAccumulator（批量缓冲）
│ (32MB缓存)  │
└──────┬──────┘
       ▼
┌─────────────┐
│ Sender线程   │ ← 批量发送（batch.size / linger.ms）
└──────┬──────┘
       ▼
    Broker

关键配置:
batch.size:        16384 (16KB)，批量发送大小
linger.ms:         5，等待时间，批量累积
buffer.memory:     33554432 (32MB)，缓存区大小
acks:              1（0-不等待，1-leader确认，all-所有ISR确认）
retries:           3，发送失败重试
max.in.flight.requests.per.connection: 5，并发请求数
```

### 2.2 消费者设计

```
消费者组 rebalance:

新消费者加入组:

Consumer 1          Group Coordinator（Broker）
   │ ─── JoinGroup ───>│
   │                   │
   │<── JoinGroup ─────│
   │  收到成员列表      │
   │                   │
   │ ─── SyncGroup ───>│
   │  分配分区方案      │
   │                   │
   │<── SyncGroup ─────│
   │  确认分配          │

分区分配策略:
Range（默认）: 按分区范围分配
   Consumer0: [0,1,2], Consumer1: [3,4,5]
   
RoundRobin: 轮询分配
   Consumer0: [0,2,4], Consumer1: [1,3,5]
   
Sticky: 尽量保持现有分配，减少变动

位移提交:
自动提交（默认5秒）: 可能重复消费/丢失消息
手动提交: 
   - commitSync(): 同步提交，可靠但阻塞
   - commitAsync(): 异步提交，高性能但可能失败
```

### 2.3 副本机制

```
副本同步:

Leader(Broker1)          Follower(Broker2)
   │                        │
   │<── Fetch 请求 ─────────│
   │  offset=100            │
   │                        │
   │── 返回消息 100-200 ──>│
   │                        │
   │<── Fetch 请求 ─────────│
   │  offset=200            │
   │  （确认已收到100-200）  │

ISR列表维护:
replica.lag.time.max.ms = 10000
如果Follower 10秒内未请求数据，移出ISR

ACKS配置:
acks=0:  生产者不等待确认，最高吞吐，可能丢失
acks=1:  等待Leader确认，平衡方案
acks=all: 等待ISR所有副本确认，最高可靠，吞吐较低
```

## 3. Kafka实战

### 3.1 Java生产者

```java
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
props.put("acks", "all");                    // 最高可靠性
props.put("retries", 3);                     // 失败重试
props.put("batch.size", 16384);              // 16KB批量
props.put("linger.ms", 5);                   // 等待5ms累积
props.put("buffer.memory", 33554432);        // 32MB缓存

Producer<String, String> producer = new KafkaProducer<>(props);

// 同步发送
producer.send(new ProducerRecord<>("topic", "key", "value")).get();

// 异步发送（带回调）
producer.send(new ProducerRecord<>("topic", "key", "value"), 
    (metadata, exception) -> {
        if (exception != null) {
            exception.printStackTrace();
        } else {
            System.out.println("Offset: " + metadata.offset());
        }
    });

// 事务消息（幂等性）
props.put("enable.idempotence", "true");     // 启用幂等
props.put("transactional.id", "my-producer"); // 事务ID

producer.initTransactions();
try {
    producer.beginTransaction();
    producer.send(record1);
    producer.send(record2);
    producer.commitTransaction();
} catch (Exception e) {
    producer.abortTransaction();
}
```

### 3.2 Java消费者

```java
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("group.id", "my-group");
props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
props.put("auto.offset.reset", "earliest");  // 无位移时从最早开始
props.put("enable.auto.commit", "false");    // 手动提交

Consumer<String, String> consumer = new KafkaConsumer<>(props);
consumer.subscribe(Arrays.asList("topic"));

try {
    while (true) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        for (ConsumerRecord<String, String> record : records) {
            System.out.printf("offset = %d, key = %s, value = %s%n",
                record.offset(), record.key(), record.value());
        }
        // 同步提交（处理完一批再提交，保证不丢失）
        consumer.commitSync();
    }
} finally {
    consumer.close();
}
```

### 3.3 Spring Kafka集成

```java
// 生产者
@Configuration
public class KafkaProducerConfig {
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }
    
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

@Service
public class OrderService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public void createOrder(Order order) {
        // 业务逻辑...
        kafkaTemplate.send("order-topic", order.getId(), JsonUtils.toJson(order));
    }
}

// 消费者
@Component
public class OrderConsumer {
    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void consume(String message) {
        Order order = JsonUtils.fromJson(message, Order.class);
        // 处理订单...
    }
    
    // 批量消费
    @KafkaListener(topics = "order-topic", batch = "true")
    public void consumeBatch(List<ConsumerRecord<String, String>> records) {
        for (ConsumerRecord<String, String> record : records) {
            // 批量处理
        }
    }
}
```

## 4. Kafka运维

### 4.1 常用命令

```bash
# 创建Topic
kafka-topics.sh --create --topic my-topic --bootstrap-server localhost:9092 \
  --partitions 3 --replication-factor 2

# 查看Topic列表
kafka-topics.sh --list --bootstrap-server localhost:9092

# 查看Topic详情
kafka-topics.sh --describe --topic my-topic --bootstrap-server localhost:9092

# 发送消息
kafka-console-producer.sh --topic my-topic --bootstrap-server localhost:9092

# 消费消息
kafka-console-consumer.sh --topic my-topic --from-beginning \
  --bootstrap-server localhost:9092

# 查看消费者组
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list

# 查看消费者组详情
kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --describe --group my-group
```

### 4.2 性能调优

```
Producer调优:
- batch.size: 根据消息大小调整（32KB~256KB）
- linger.ms: 5~100ms，增大可提升吞吐
- compression.type: lz4/snappy（压缩减少网络传输）
- buffer.memory: 根据内存调整（64MB~256MB）

Consumer调优:
- max.poll.records: 每次拉取消息数（默认500）
- fetch.min.bytes / fetch.max.wait.ms: 控制拉取频率
- session.timeout.ms / heartbeat.interval.ms: 心跳配置

Broker调优:
- num.network.threads: 网络线程数（默认3）
- num.io.threads: IO线程数（默认8）
- log.segment.bytes: 日志段大小（默认1GB）
- log.retention.hours: 日志保留时间（默认168小时=7天）
```

## 5. 最佳实践

1. **Topic分区数合理规划**：初期 3~6 个，后续可扩容
2. **Key设计**：相同Key的消息进入同一分区，保证顺序
3. **消费者组命名规范**：业务-环境-功能（如 order-prod-consumer）
4. **监控指标**：
   - UnderReplicatedPartitions（副本不足）
   - OfflinePartitions（离线分区）
   - ConsumerLag（消费延迟）
5. **数据保留策略**：按时间或大小清理，避免磁盘满
6. **多集群部署**：生产/消费分离，跨机房复制（MirrorMaker）