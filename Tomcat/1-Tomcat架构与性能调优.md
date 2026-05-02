# Tomcat架构与性能调优

## 1. Tomcat 架构

```
Tomcat 整体架构:

Server（服务器）
└── Service（服务）
    ├── Connector（连接器）
    │   └── ProtocolHandler
    │       ├── EndPoint（网络I/O）
    │       │   └── Acceptor → Poller → Worker
    │       └── Processor（协议处理）
    │           └── HTTP/1.1 Processor
    └── Container（容器）
        └── Engine（引擎）
            └── Host（虚拟主机）
                └── Context（应用上下文）
                    └── Wrapper（Servlet包装器）

请求处理流程:

Client Request
    │
    ▼
Connector:EndPoint → 接收网络请求（NIO/NIO2/APR）
    │
    ▼
Connector:Processor → 解析HTTP协议
    │
    ▼
CoyoteAdapter → 适配到Servlet容器
    │
    ▼
Engine → Host → Context → Wrapper
    │
    ▼
FilterChain → Servlet
    │
    ▼
Business Logic
```

## 2. Connector 详解

### 2.1 三种 I/O 模式

| 模式 | 特性 | 适用场景 |
|------|------|---------|
| **BIO** | 阻塞式，一个请求一个线程 | Tomcat 7 及以下 |
| **NIO** | 非阻塞式，少量线程处理多连接 | 默认，高并发 |
| **NIO2** | 异步 I/O（JDK 7+） | 更高并发 |
| **APR** | 本地库（OpenSSL） | 需要 HTTPS 性能 |

```xml
<!-- server.xml -->
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443" />

<!-- NIO 显式配置 -->
<Connector port="8080" 
           protocol="org.apache.coyote.http11.Http11NioProtocol"
           maxThreads="500"
           minSpareThreads="50"
           acceptCount="1000"
           enableLookups="false"
           compression="on"
           compressionMinSize="2048"
           compressableMimeType="text/html,text/xml,text/plain,text/css,application/json" />
```

### 2.2 线程模型

```
NIO 线程模型:

Acceptor Thread (1个)
    │
    ├── 接收新连接
    └── 注册到 Poller

Poller Thread (1-2个，CPU核数)
    │
    ├── 轮询 Selector
    ├── 检测可读事件
    └── 分发到 Executor

Executor Thread Pool (N个)
    │
    ├── 解析 HTTP 请求
    ├── 调用 Servlet
    └── 返回响应

关键参数:
maxThreads:        最大线程数（默认200）
minSpareThreads:   最小空闲线程数（默认10）
maxConnections:    最大连接数（NIO默认10000）
acceptCount:       等待队列长度（默认100）
```

## 3. Container 容器

```
容器层级:

Engine: Catalina（默认）
├── Host: localhost
│   ├── Context: /app1
│   │   └── Wrapper: Servlet1, Servlet2...
│   └── Context: /app2
└── Host: www.example.com
    └── Context: /

生命周期:

new → init() → start() → running → stop() → destroy()

各容器职责:
Engine: 接收 Connector 请求，分发给 Host
Host:   基于域名匹配（虚拟主机）
Context: 对应一个 Web 应用（一个 WAR）
Wrapper: 对应一个 Servlet
```

## 4. 类加载机制

```
Tomcat 类加载器（违背双亲委派）:

Bootstrap ClassLoader
    ↑
System ClassLoader
    ↑
Common ClassLoader  (CATALINA_BASE/lib)
    ↑
    ├── Catalina ClassLoader  (CATALINA_HOME/lib)
    └── Shared ClassLoader
            ↑
    Webapp ClassLoader  (WEB-INF/classes, WEB-INF/lib)

为什么违背双亲委派？
- Web 应用隔离：不同应用使用不同版本的 Spring
- 热部署：重新加载 Webapp ClassLoader
- 优先加载 Web 应用自己的类

热部署原理:
1. 停止旧 Context
2. 创建新的 Webapp ClassLoader
3. 加载新的类
4. 启动新 Context
```

## 5. 性能调优

### 5.1 连接数优化

```xml
<!-- server.xml -->
<Connector port="8080" protocol="org.apache.coyote.http11.Http11Nio2Protocol"
           maxThreads="800"
           minSpareThreads="100"
           maxConnections="20000"
           acceptCount="1000"
           connectionTimeout="20000"
           maxHttpHeaderSize="8192"
           compression="on"
           compressionMinSize="1024"
           compressableMimeType="text/html,text/xml,text/plain,application/json" />

参数说明:
maxThreads:        处理请求的最大线程数
                   公式: (最大QPS * 平均响应时间) / 1000
                   示例: (1000 * 200ms) / 1000 = 200

minSpareThreads:   最小空闲线程，避免频繁创建

maxConnections:    NIO模式下最大并发连接（Bio模式=maxThreads）
                   超过后放入 acceptCount 队列

acceptCount:       等待队列长度，超过后拒绝连接
                   建议: 根据系统处理能力设置
```

### 5.2 内存优化

```bash
# setenv.sh（Linux）
export CATALINA_OPTS="
    -server
    -Xms4g -Xmx4g                          # 堆内存
    -XX:MetaspaceSize=256m                 # 元空间初始
    -XX:MaxMetaspaceSize=512m              # 元空间最大
    -Xmn1536m                              # 新生代（堆的 3/8）
    -XX:+UseG1GC                           # G1垃圾回收器
    -XX:MaxGCPauseMillis=200               # 最大GC停顿
    -XX:+HeapDumpOnOutOfMemoryError        # OOM时生成堆转储
    -XX:HeapDumpPath=/var/log/tomcat/      # 堆转储路径
    -XX:+PrintGCDetails                    # GC日志
    -XX:+PrintGCDateStamps
    -Xloggc:/var/log/tomcat/gc.log
    -XX:+UseGCLogFileRotation
    -XX:NumberOfGCLogFiles=10
    -XX:GCLogFileSize=100M
"
```

### 5.3 线程池优化

```xml
<!-- 使用共享 Executor -->
<Executor name="tomcatThreadPool" 
          namePrefix="catalina-exec-"
          maxThreads="800"
          minSpareThreads="100"
          maxIdleTime="60000"
          maxQueueSize="1000"
          prestartminSpareThreads="true" />

<Connector executor="tomcatThreadPool"
           port="8080" 
           protocol="org.apache.coyote.http11.Http11Nio2Protocol" />
```

### 5.4 数据库连接池（应用层）

```xml
<!-- context.xml -->
<Resource name="jdbc/MyDB"
          auth="Container"
          type="javax.sql.DataSource"
          factory="com.zaxxer.hikari.HikariJNDIFactory"
          jdbcUrl="jdbc:mysql://localhost:3306/mydb"
          username="user"
          password="pass"
          driverClassName="com.mysql.cj.jdbc.Driver"
          maximumPoolSize="50"
          minimumIdle="10"
          idleTimeout="300000"
          maxLifetime="1800000"
          connectionTimeout="30000"
          leakDetectionThreshold="60000" />
```

## 6. 嵌入式 Tomcat（Spring Boot）

```java
// Spring Boot 自动配置嵌入式 Tomcat
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// 自定义配置
@Bean
public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
    return factory -> {
        factory.addConnectorCustomizers(connector -> {
            connector.setMaxThreads(500);
            connector.setMinSpareThreads(50);
            connector.setMaxConnections(10000);
            connector.setAcceptCount(1000);
        });
        
        factory.addContextCustomizers(context -> {
            context.setReloadable(false);  // 生产环境禁用热加载
        });
    };
}
```

```yaml
# application.yml 配置
server:
  tomcat:
    threads:
      max: 500                    # 最大线程数
      min-spare: 50               # 最小空闲线程
    max-connections: 10000        # 最大连接数
    accept-count: 1000            # 等待队列
    connection-timeout: 20000     # 连接超时
    max-http-form-post-size: 2MB  # POST最大大小
    accesslog:
      enabled: true
      pattern: '%h %l %u %t "%r" %s %b %D'
      directory: /var/log/tomcat
```

## 7. 监控指标

```
关键指标:

线程指标:
- tomcat_threads_current: 当前线程数
- tomcat_threads_busy: 忙碌线程数
- tomcat_threads_config_max: 最大线程配置

连接指标:
- tomcat_connections_current: 当前连接数
- tomcat_connections_max: 最大连接数

请求指标:
- tomcat_requests_count_total: 总请求数
- tomcat_requests_processing: 处理中请求数

错误指标:
- tomcat_errors_count_total: 错误数

JMX 获取:
JMX ObjectName: Catalina:type=ThreadPool,name="http-nio-8080"
- connectionCount
- currentThreadCount
- currentThreadsBusy
- maxThreads
```

## 8. 故障排查

```
常见问题:

1. 连接数耗尽
症状: 请求超时，大量等待
解决: 增加 maxThreads / 优化响应时间 / 扩容

2. 内存溢出
症状: OOM，频繁 Full GC
解决: 增加堆内存 / 检查内存泄漏 / 优化代码

3. 线程死锁
症状: 响应变慢，CPU 低但请求堆积
解决: jstack 分析线程状态 / 检查同步代码

4. 类加载泄漏
症状: 多次部署后 PermGen/Metaspace 满
解决: 检查 ThreadLocal / 监听器注销

排查工具:
- jstack: 线程Dump
- jmap: 堆转储
- jstat: GC统计
- VisualVM: 可视化监控
```

## 9. 安全加固

```xml
<!-- 1. 关闭管理接口 -->
<!-- 删除或注释掉 -->
<!-- <Context privileged="true" /> -->

<!-- 2. 禁用自动部署 -->
<Host name="localhost" appBase="webapps"
      unpackWARs="false" autoDeploy="false" />

<!-- 3. 隐藏版本号 -->
<Valve className="org.apache.catalina.valves.ErrorReportValve"
       showReport="false" showServerInfo="false" />

<!-- 4. 配置HTTPS -->
<Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
           maxThreads="150" SSLEnabled="true" scheme="https" secure="true"
           clientAuth="false" sslProtocol="TLS"
           keystoreFile="conf/keystore.jks"
           keystorePass="password" />

<!-- 5. 访问日志 -->
<Valve className="org.apache.catalina.valves.AccessLogValve"
       directory="logs" prefix="access_log"
       suffix=".txt" pattern="%h %l %u %t &quot;%r&quot; %s %b %D" />
```