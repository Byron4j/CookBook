# Prometheus+Grafana监控体系

## 1. 概述

Prometheus 是一个开源的系统监控和告警工具包，以其强大的多维数据模型和灵活的查询语言 PromQL 而闻名。

```
监控体系架构:

┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Application │     │  Application │     │  Application │
│   (Metrics)  │     │   (Metrics)  │     │   (Metrics)  │
└──────┬───────┘     └──────┬───────┘     └──────┬───────┘
       │                    │                    │
       └────────────────────┼────────────────────┘
                            │ Pull /metrics
                            ▼
                  ┌─────────────────────┐
                  │     Prometheus      │
                  │  - 时序数据库        │
                  │  - 告警规则          │
                  │  - 主动拉取          │
                  └──────────┬──────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
        ┌─────────┐   ┌─────────┐   ┌─────────┐
        │ Grafana │   │ Alert   │   │ Push    │
        │(可视化) │   │Manager  │   │Gateway  │
        └─────────┘   └─────────┘   └─────────┘
```

## 2. Prometheus 核心概念

### 2.1 数据模型

```
时间序列（Time Series）:

由指标名（Metric Name）和标签（Labels）唯一标识:

http_requests_total{method="GET", endpoint="/api/users", status="200"}
http_requests_total{method="POST", endpoint="/api/users", status="201"}
http_requests_total{method="GET", endpoint="/api/orders", status="500"}

四种指标类型:

Counter（计数器）:
- 单调递增（只能增加或重置为0）
- 适用于：请求数、错误数、任务完成数
- 示例: http_requests_total

Gauge（仪表盘）:
- 可增可减
- 适用于：温度、内存使用量、当前连接数
- 示例: node_memory_usage_bytes

Histogram（直方图）:
- 采样观测值并分桶计数
- 自动计算 _sum、_count、_bucket
- 适用于：请求延迟、响应大小
- 示例: http_request_duration_seconds

Summary（摘要）:
- 类似Histogram，但计算滑动时间窗口内的分位数
- 适用于：需要精确分位数的场景
```

### 2.2 PromQL 查询

```promql
# 基础查询
http_requests_total                                    # 所有时间序列
http_requests_total{method="GET"}                      # 按标签过滤
http_requests_total{method=~"GET|POST"}                # 正则匹配

# 范围查询（过去5分钟）
http_requests_total[5m]

# 聚合操作
sum(http_requests_total)                               # 总和
avg(http_request_duration_seconds)                     # 平均值
max(node_cpu_usage)                                    # 最大值
count(up)                                              # 计数

# 按标签分组聚合
sum by (method) (http_requests_total)                  # 按 method 分组求和
sum without (instance) (http_requests_total)           # 排除 instance 标签

# 数学运算
http_requests_total / 1024                             # 单位转换
(node_memory_total - node_memory_free) / node_memory_total * 100  # 内存使用率

# 常用函数
rate(http_requests_total[5m])                          # 每秒增长率
irate(http_requests_total[5m])                         # 瞬时增长率
increase(http_requests_total[1h])                      # 1小时内增量
delta(cpu_temp[1h])                                    # 1小时内差值
histogram_quantile(0.99, rate(http_request_duration_bucket[5m]))  # P99延迟

# 比较和逻辑
up == 0                                                # 宕机实例
http_requests_total > 1000                            # 大于1000的
up and on(instance) node_cpu_usage > 80                 # 多条件联合
```

## 3. Spring Boot 集成

### 3.1 引入依赖

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 3.2 配置

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus  # 暴露prometheus端点
  endpoint:
    metrics:
      enabled: true
    prometheus:
      enabled: true
  metrics:
    tags:
      application: ${spring.application.name}     # 添加全局标签
    distribution:
      percentiles-histogram:
        http.server.requests: true                # 启用直方图
      slo:
        http.server.requests: 50ms,100ms,200ms,500ms,1s,5s  # SLO定义
```

### 3.3 自定义指标

```java
@Component
public class OrderMetrics {
    
    private final Counter orderCounter;
    private final Timer orderTimer;
    private final Gauge stockGauge;
    private final AtomicInteger stockValue = new AtomicInteger(0);
    
    public OrderMetrics(MeterRegistry registry) {
        // Counter: 订单总数
        this.orderCounter = Counter.builder("orders.created")
            .description("Total orders created")
            .tags("channel", "web")
            .register(registry);
        
        // Timer: 订单处理耗时
        this.orderTimer = Timer.builder("orders.processing.time")
            .description("Order processing time")
            .register(registry);
        
        // Gauge: 当前库存
        this.stockGauge = Gauge.builder("inventory.stock")
            .description("Current stock level")
            .register(registry, stockValue, AtomicInteger::get);
    }
    
    public void recordOrder() {
        orderCounter.increment();
    }
    
    public void recordProcessingTime(Duration duration) {
        orderTimer.record(duration);
    }
    
    public void updateStock(int stock) {
        stockValue.set(stock);
    }
}
```

## 4. Prometheus 配置

```yaml
# prometheus.yml
global:
  scrape_interval: 15s      # 默认抓取间隔
  evaluation_interval: 15s  # 规则评估间隔

alerting:
  alertmanagers:
    - static_configs:
        - targets: ['alertmanager:9093']

rule_files:
  - "alert_rules.yml"

scrape_configs:
  # 监控 Prometheus 自身
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  # 监控 Spring Boot 应用
  - job_name: 'spring-boot-apps'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['app1:8080', 'app2:8080']
    
  # 监控服务器（Node Exporter）
  - job_name: 'node-exporter'
    static_configs:
      - targets: ['node1:9100', 'node2:9100']

  # 监控 MySQL（mysqld_exporter）
  - job_name: 'mysql'
    static_configs:
      - targets: ['mysql-exporter:9104']

  # 监控 Redis（redis_exporter）
  - job_name: 'redis'
    static_configs:
      - targets: ['redis-exporter:9121']

  # 服务发现（Kubernetes）
  - job_name: 'kubernetes-pods'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
```

## 5. 告警规则

```yaml
# alert_rules.yml
groups:
  - name: application-alerts
    rules:
      # 服务宕机
      - alert: ServiceDown
        expr: up == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "服务 {{ $labels.instance }} 宕机"
          description: "服务 {{ $labels.instance }} 已宕机超过1分钟"

      # 错误率高
      - alert: HighErrorRate
        expr: |
          sum(rate(http_requests_total{status=~"5.."}[5m])) 
          / sum(rate(http_requests_total[5m])) > 0.05
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "错误率过高"
          description: "错误率超过 5%，当前值: {{ $value }}"

      # P99延迟高
      - alert: HighLatency
        expr: histogram_quantile(0.99, rate(http_request_duration_seconds_bucket[5m])) > 1
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "P99延迟过高"
          description: "P99延迟超过 1s，当前值: {{ $value }}s"

      # CPU使用率高
      - alert: HighCPUUsage
        expr: 100 - (avg by(instance) (irate(node_cpu_seconds_total{mode="idle"}[5m])) * 100) > 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "CPU使用率高"
          description: "CPU使用率超过 80%，当前值: {{ $value }}%"

      # 内存使用率高
      - alert: HighMemoryUsage
        expr: (node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / node_memory_MemTotal_bytes * 100 > 85
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "内存使用率高"
          description: "内存使用率超过 85%，当前值: {{ $value }}%"

      # 磁盘使用率高
      - alert: HighDiskUsage
        expr: (node_filesystem_size_bytes - node_filesystem_avail_bytes) / node_filesystem_size_bytes * 100 > 90
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "磁盘使用率高"
          description: "磁盘使用率超过 90%，当前值: {{ $value }}%"
```

## 6. Grafana 仪表盘

### 6.1 常用面板

```
仪表盘设计:

Row 1: 概览
- 当前 QPS (Counter)
- 平均响应时间 (Gauge)
- 错误率 (Gauge)
- 在线用户数 (Gauge)

Row 2: 流量
- QPS 趋势图 (Time Series)
- 状态码分布 (Pie Chart)
- Top 10 慢接口 (Table)

Row 3: 延迟
- P50/P95/P99 延迟 (Time Series)
- 延迟直方图 (Heatmap)

Row 4: 资源
- CPU 使用率 (Time Series)
- 内存使用率 (Time Series)
- 磁盘 I/O (Time Series)
- 网络流量 (Time Series)

Row 5: JVM
- 堆内存使用 (Time Series)
- GC 次数和耗时 (Time Series)
- 线程数 (Time Series)
```

### 6.2 常用 PromQL

```promql
# QPS
sum(rate(http_requests_total[1m]))

# 错误率
sum(rate(http_requests_total{status=~"5.."}[5m])) / sum(rate(http_requests_total[5m]))

# P99延迟
histogram_quantile(0.99, sum(rate(http_request_duration_seconds_bucket[5m])) by (le))

# JVM内存
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}

# GC耗时
increase(jvm_gc_pause_seconds_sum[1m]) / increase(jvm_gc_pause_seconds_count[1m])

# 线程数
jvm_threads_live_threads
```

## 7. Docker Compose 部署

```yaml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - ./alert_rules.yml:/etc/prometheus/alert_rules.yml
      - prometheus-data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--storage.tsdb.retention.time=15d'
    ports:
      - "9090:9090"

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    volumes:
      - grafana-data:/var/lib/grafana
      - ./grafana-dashboards:/etc/grafana/provisioning/dashboards
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
      - GF_USERS_ALLOW_SIGN_UP=false
    ports:
      - "3000:3000"
    depends_on:
      - prometheus

  alertmanager:
    image: prom/alertmanager:latest
    container_name: alertmanager
    volumes:
      - ./alertmanager.yml:/etc/alertmanager/alertmanager.yml
    ports:
      - "9093:9093"

  node-exporter:
    image: prom/node-exporter:latest
    container_name: node-exporter
    volumes:
      - /proc:/host/proc:ro
      - /sys:/host/sys:ro
      - /:/rootfs:ro
    command:
      - '--path.procfs=/host/proc'
      - '--path.rootfs=/rootfs'
      - '--path.sysfs=/host/sys'
    ports:
      - "9100:9100"

volumes:
  prometheus-data:
  grafana-data:
```

## 8. 最佳实践

1. **指标命名规范**：
   - 使用 `<namespace>_<subsystem>_<metric_name>_<unit>_<suffix>`
   - 示例: `http_requests_total`, `orders_processing_duration_seconds`

2. **标签设计**：
   - 标签值基数不要过高（避免高 cardinalities）
   - 不要在标签中使用用户ID、订单ID等无界值
   - 合理分组（按服务、实例、接口、状态码）

3. **采样间隔**：
   - 业务指标：15s - 30s
   - 系统指标：15s
   - 日志指标：60s

4. **数据保留**：
   - 原始数据：15天
   - 聚合数据（小时级）：3个月
   - 聚合数据（天级）：1年

5. **告警原则**：
   - 少而精，避免告警疲劳
   - 分级处理（P0/P1/P2）
   - 可操作的告警（包含解决方案链接）
   - 告警恢复通知