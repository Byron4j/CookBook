# ELK日志采集与分析

## 1. 概述

ELK Stack 是一套开源的日志分析解决方案，由三个核心组件组成：

- **Elasticsearch**：分布式搜索引擎，存储和查询日志
- **Logstash**：日志收集和处理管道
- **Kibana**：可视化分析界面

```
架构图:

应用服务 → Filebeat → Logstash → Elasticsearch → Kibana
              │           │            │             │
           轻量采集     过滤解析      存储索引        可视化

或者简化架构（无需复杂处理）:

应用服务 → Filebeat → Elasticsearch → Kibana
```

## 2. Elasticsearch

### 2.1 核心概念

```
关系型数据库 vs Elasticsearch:

MySQL              Elasticsearch
─────────          ─────────────
Database     ↔     Index（索引）
Table        ↔     Type（类型，已废弃）
Row          ↔     Document（文档）
Column       ↔     Field（字段）
Schema       ↔     Mapping（映射）
SQL          ↔     DSL（查询语言）

倒排索引（Inverted Index）:

传统索引（正向）:
文档1: "Java is good"
文档2: "Java and Python"

倒排索引:
Java    → [文档1, 文档2]
is      → [文档1]
good    → [文档1]
and     → [文档2]
Python  → [文档2]

搜索 "Java" → 直接命中 [文档1, 文档2]
```

### 2.2 基本操作

```bash
# 查看集群健康
curl -X GET "localhost:9200/_cluster/health"

# 创建索引
curl -X PUT "localhost:9200/my_index" -H 'Content-Type: application/json' -d'
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "timestamp": { "type": "date" },
      "level": { "type": "keyword" },
      "message": { "type": "text" },
      "service": { "type": "keyword" }
    }
  }
}'

# 插入文档
curl -X POST "localhost:9200/my_index/_doc" -H 'Content-Type: application/json' -d'
{
  "timestamp": "2024-01-01T12:00:00",
  "level": "ERROR",
  "message": "Connection timeout",
  "service": "order-service"
}'

# 搜索
curl -X GET "localhost:9200/my_index/_search" -H 'Content-Type: application/json' -d'
{
  "query": {
    "match": {
      "message": "timeout"
    }
  },
  "sort": [
    { "timestamp": "desc" }
  ]
}'
```

### 2.3 分片与副本

```
分片（Shard）:
- 数据水平拆分单元
- 主分片（Primary Shard）：写入数据
- 副本分片（Replica Shard）：读数据、容错

示例:
索引: logs
主分片: 3 个
副本: 1 个

Node 1: [P0, R1]    P=Primary, R=Replica
Node 2: [P1, R2]
Node 3: [P2, R0]

写入流程:
1. 请求到 Node 1（协调节点）
2. 计算路由: shard = hash(routing) % number_of_primary_shards
3. 写入主分片 P0
4. 同步到副本 R0
5. 返回成功
```

## 3. Logstash

### 3.1 配置文件

```ruby
# logstash.conf
input {
  beats {
    port => 5044
  }
  
  # 也可以直接读取文件
  file {
    path => "/var/log/app/*.log"
    start_position => "beginning"
  }
}

filter {
  # 解析 JSON 日志
  if [message] =~ "^\{" {
    json {
      source => "message"
    }
  }
  
  # Grok 解析非结构化日志
  grok {
    match => {
      "message" => "%{TIMESTAMP_ISO8601:timestamp} %{LOGLEVEL:level} %{DATA:logger} - %{GREEDYDATA:msg}"
    }
  }
  
  # 日期解析
  date {
    match => ["timestamp", "ISO8601"]
    target => "@timestamp"
  }
  
  # 删除无用字段
  mutate {
    remove_field => ["message", "timestamp", "host"]
  }
  
  # IP 地理位置
  geoip {
    source => "client_ip"
  }
}

output {
  elasticsearch {
    hosts => ["http://localhost:9200"]
    index => "app-logs-%{+YYYY.MM.dd}"
    # 按天分索引，便于管理和删除
  }
  
  # 同时输出到控制台（调试用）
  stdout { codec => rubydebug }
}
```

### 3.2 Grok 模式

```
常用 Grok 模式:

%{IP:client_ip}           → 172.16.0.1
%{WORD:method}            → GET, POST
%{URIPATHPARAM:request}   → /api/users/123
%{NUMBER:status:int}      → 200, 404
%{NUMBER:duration:float}  → 0.023
%{DATA:message}           → 任意字符串
%{GREEDYDATA:message}     → 贪婪匹配到结尾

示例日志:
172.16.0.1 - - [01/Jan/2024:12:00:00 +0800] "GET /api/users HTTP/1.1" 200 1024 0.045

Grok 模式:
%{IP:client_ip} - - \[%{HTTPDATE:timestamp}\] "%{WORD:method} %{URIPATHPARAM:request} HTTP/%{NUMBER:http_version}" %{NUMBER:status:int} %{NUMBER:bytes:int} %{NUMBER:duration:float}
```

## 4. Filebeat

### 4.1 配置

```yaml
# filebeat.yml
filebeat.inputs:
- type: log
  enabled: true
  paths:
    - /var/log/app/*.log
  fields:
    service: order-service
    env: production
  fields_under_root: true
  multiline.pattern: '^\['  # 多行日志合并（以 [ 开头）
  multiline.negate: true
  multiline.match: after

- type: log
  enabled: true
  paths:
    - /var/log/nginx/*.log
  fields:
    service: nginx
    
processors:
- add_host_metadata:
    when.not.contains.tags: forwarded
- add_cloud_metadata: ~
- add_docker_metadata: ~
- add_kubernetes_metadata: ~

output.elasticsearch:
  hosts: ["http://localhost:9200"]
  indices:
    - index: "app-logs-%{+yyyy.MM.dd}"
      when.contains:
        service: "order"
    - index: "nginx-logs-%{+yyyy.MM.dd}"
      when.contains:
        service: "nginx"

# 输出到 Logstash（需要复杂处理时）
# output.logstash:
#   hosts: ["localhost:5044"]
```

## 5. Kibana

### 5.1 核心功能

```
Discover: 日志检索
- 搜索框输入查询条件: level:ERROR AND service:order-service
- 时间范围选择: Last 15 minutes / Last 7 days
- 字段筛选和排序

Visualize: 可视化
- 柱状图: 每小时错误数
- 饼图: 错误级别分布
- 折线图: QPS 趋势
- 热力图: 接口延迟分布

Dashboard: 仪表盘
- 组合多个可视化图表
- 实时刷新
- 全屏展示

Alerting: 告警（需插件或 Elastic Stack）
- 错误数超过阈值发邮件
- P99 延迟超过 500ms 告警
```

### 5.2 常用查询 DSL

```json
{
  "query": {
    "bool": {
      "must": [
        { "match": { "level": "ERROR" } },
        { "range": { "@timestamp": { "gte": "now-1h" } } }
      ],
      "must_not": [
        { "match": { "message": "healthcheck" } }
      ]
    }
  },
  "aggs": {
    "by_service": {
      "terms": { "field": "service.keyword" }
    }
  }
}
```

## 6. Docker Compose 部署

```yaml
version: '3.8'

services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports:
      - "9200:9200"
    volumes:
      - es-data:/usr/share/elasticsearch/data

  logstash:
    image: docker.elastic.co/logstash/logstash:8.11.0
    container_name: logstash
    volumes:
      - ./logstash.conf:/usr/share/logstash/pipeline/logstash.conf
    ports:
      - "5044:5044"
    depends_on:
      - elasticsearch

  kibana:
    image: docker.elastic.co/kibana/kibana:8.11.0
    container_name: kibana
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
    ports:
      - "5601:5601"
    depends_on:
      - elasticsearch

  filebeat:
    image: docker.elastic.co/beats/filebeat:8.11.0
    container_name: filebeat
    volumes:
      - ./filebeat.yml:/usr/share/filebeat/filebeat.yml
      - /var/log:/var/log:ro
    depends_on:
      - logstash

volumes:
  es-data:
```

## 7. 最佳实践

1. **索引生命周期管理（ILM）**：
   - 热数据（7天内）：高性能存储
   - 温数据（7-30天）：普通存储
   - 冷数据（30-90天）：压缩存储
   - 删除（90天后）：自动删除

2. **日志规范**：
   - 统一 JSON 格式
   - 包含 traceId（链路追踪）
   - 分级记录（DEBUG/INFO/WARN/ERROR）
   - 敏感信息脱敏

3. **性能优化**：
   - 批量写入（Bulk API）
   - 合理设置分片数
   - 使用 Filebeat 替代 Logstash（轻量）
   - 索引模板预定义 Mapping

4. **监控告警**：
   - 错误日志突增告警
   - 慢查询日志监控
   - ES 集群状态监控
   - 磁盘使用率告警