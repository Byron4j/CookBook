# Seata分布式事务实战

## 1. 概念

Seata（Simple Extensible Autonomous Transaction Architecture）是阿里巴巴开源的分布式事务解决方案，致力于提供高性能和易用的分布式事务服务。

```
分布式事务场景:

单体应用（本地事务）:
┌─────────────────────────────────┐
│         订单服务                 │
│  ┌─────────┐   ┌─────────────┐ │
│  │ 扣库存   │   │ 创建订单     │ │
│  │（DB A）  │   │（DB B）     │ │
│  └─────────┘   └─────────────┘ │
│        @Transactional           │
└─────────────────────────────────┘
一个数据库连接，ACID保证

微服务（分布式事务）:
┌───────────┐    ┌───────────┐    ┌───────────┐
│  订单服务  │───→│  库存服务  │    │  账户服务  │
│ （DB A）  │    │ （DB B）  │───→│ （DB C）  │
└───────────┘    └───────────┘    └───────────┘
多个数据库，需要分布式事务保证一致性
```

## 2. Seata 架构

```
Seata 三大组件:

TC (Transaction Coordinator) - 事务协调器
├── 维护全局事务的运行状态
├── 负责全局提交或回滚
└── 独立部署的服务

TM (Transaction Manager) - 事务管理器
├── 定义全局事务的范围
├── 负责全局事务的提交/回滚决议
└── 在业务应用中嵌入

RM (Resource Manager) - 资源管理器
├── 管理分支事务
├── 向 TC 注册并报告状态
└── 在各微服务中嵌入

工作流程:

TM                  TC                  RM
 │  1. begin          │                   │
 │ ────────────────> │                   │
 │                   │                   │
 │  2. 执行业务        │                   │
 │ ──────────────────────────────────────>│
 │                   │  3. register       │
 │                   │ <───────────────────│
 │                   │                   │
 │  4. commit/rollback│                  │
 │ ────────────────> │                   │
 │                   │  5. 通知各RM       │
 │                   │ ─────────────────>│
 │                   │                   │
 │                   │  6. 二阶段提交      │
 │                   │ <───────────────────│
```

## 3. 事务模式

### 3.1 AT 模式（Automatic Transaction）

```
AT 模式原理:

一阶段:
1. 解析 SQL，生成查询前镜像（Before Image）
2. 执行业务 SQL
3. 查询后镜像（After Image）
4. 插入回滚日志（Undo Log）

二阶段（成功）:
- 异步删除 Undo Log

二阶段（回滚）:
- 读取 Undo Log
- 生成反向 SQL（如 INSERT → DELETE）
- 执行反向 SQL 恢复数据

示例:

业务 SQL: UPDATE product SET stock = stock - 1 WHERE id = 1;

一阶段:
1. 查询前镜像: SELECT stock FROM product WHERE id = 1; → stock=100
2. 执行 UPDATE
3. 查询后镜像: SELECT stock FROM product WHERE id = 1; → stock=99
4. 记录 Undo Log:
   {
     "beforeImage": {"stock": 100},
     "afterImage": {"stock": 99},
     "sqlType": "UPDATE",
     "tableName": "product"
   }

回滚时:
UPDATE product SET stock = 100 WHERE id = 1;
```

### 3.2 TCC 模式

```
TCC（Try-Confirm-Cancel）:

Try:   预留资源（执行业务检查，锁定资源）
Confirm: 确认执行业务（真正执行业务）
Cancel:  取消执行，释放预留资源

订单服务示例:

Try:
  - 检查订单是否合法
  - 插入订单记录，状态为"处理中"
  - 冻结库存（预扣）

Confirm:
  - 更新订单状态为"已确认"
  - 确认扣减库存

Cancel:
  - 更新订单状态为"已取消"
  - 释放冻结库存

幂等性保证:
每个操作都需要保证幂等，防止网络重试导致重复执行
```

### 3.3 Saga 模式

```
Saga（长事务）:

将一个长事务拆分为多个本地事务
每个本地事务有对应的补偿操作

正向流程:  T1 → T2 → T3 → T4
补偿流程:  T4被T3补偿 ← T3被T2补偿 ← T2被T1补偿

适用场景:
- 业务流程长，需要异步化
- 需要与外部系统交互
- 对一致性要求不是实时的

两种实现:
1. 编排式（Orchestration）: 中心协调器统一调度
2. 协同式（Choreography）: 各服务间通过事件驱动
```

## 4. Spring Cloud 集成

### 4.1 配置 Seata

```yaml
# application.yml
seata:
  enabled: true
  application-id: ${spring.application.name}
  tx-service-group: my_tx_group
  service:
    vgroup-mapping:
      my_tx_group: default
    grouplist:
      default: 127.0.0.1:8091
  client:
    rm:
      async-commit-buffer-limit: 10000
    tm:
      commit-retry-count: 5
      rollback-retry-count: 5
```

### 4.2 业务代码

```java
@Service
public class OrderService {
    
    @Autowired
    private OrderDao orderDao;
    
    @Autowired
    private StorageFeignClient storageFeignClient;
    
    @Autowired
    private AccountFeignClient accountFeignClient;
    
    /**
     * @GlobalTransactional: 开启全局事务
     * name: 事务名称
     * rollbackFor: 触发回滚的异常
     * timeoutMills: 超时时间（毫秒）
     */
    @GlobalTransactional(name = "create-order", 
                         rollbackFor = Exception.class,
                         timeoutMills = 300000)
    public void create(Order order) {
        // 1. 创建订单
        orderDao.create(order);
        
        // 2. 扣减库存
        storageFeignClient.decrease(order.getProductId(), order.getCount());
        
        // 3. 扣减账户余额
        accountFeignClient.decrease(order.getUserId(), order.getMoney());
        
        // 任意步骤异常，全局回滚
    }
}
```

### 4.3 数据库配置

```sql
-- 每个参与分布式事务的数据库都需要创建 undo_log 表
CREATE TABLE IF NOT EXISTS `undo_log`
(
    `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
    `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB COMMENT ='AT transaction mode undo table';
```

## 5. 部署模式

### 5.1 单机模式

```bash
# 下载 Seata Server
wget https://github.com/seata/seata/releases/download/v1.7.0/seata-server-1.7.0.tar.gz

# 启动（文件存储）
sh bin/seata-server.sh -p 8091 -h 127.0.0.1 -m file

# 启动（DB存储）
sh bin/seata-server.sh -p 8091 -h 127.0.0.1 -m db
```

### 5.2 高可用部署（Nacos）

```yaml
# seata/conf/registry.conf
registry {
  type = "nacos"
  nacos {
    application = "seata-server"
    serverAddr = "nacos:8848"
    group = "SEATA_GROUP"
    namespace = ""
    cluster = "default"
  }
}

config {
  type = "nacos"
  nacos {
    serverAddr = "nacos:8848"
    group = "SEATA_GROUP"
    namespace = ""
  }
}
```

## 6. 最佳实践

1. **选择合适的模式**：
   - AT 模式：简单场景，无复杂 SQL（不支持多表 JOIN UPDATE）
   - TCC 模式：性能要求高，业务逻辑复杂
   - Saga 模式：长事务，异步流程

2. **避免大事务**：
   - 分布式事务范围尽量小
   - 异步化非核心业务
   - 最终一致性替代强一致性

3. **幂等性设计**：
   - 所有接口幂等
   - 防重表或唯一索引
   - Token 机制防重复提交

4. **监控告警**：
   - TC 事务状态监控
   - 异常事务及时处理
   - 超时事务人工介入

5. **性能优化**：
   - 异步提交（AT模式默认）
   - 批量处理
   - 连接池优化
   - 网络优化（同城多活）