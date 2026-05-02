# SpringBoot起步依赖与Actuator

## 1. 起步依赖（Starter）

### 1.1 概念

起步依赖（Starter）是 SpringBoot 的 Maven/Gradle 依赖描述符，将一组相关的依赖打包在一起，简化依赖管理。

```
传统方式 vs Starter 方式:

传统 Maven:
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.x</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.12.x</version>
</dependency>
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.2.x</version>
</dependency>
<!-- 还需要处理版本兼容性... -->

SpringBoot Starter:
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- 自动引入所有相关依赖，版本由 parent POM 统一管理 -->
```

### 1.2 常见 Starter

| Starter | 功能 | 包含依赖 |
|---------|------|---------|
| `spring-boot-starter-web` | Web 应用（含 MVC） | spring-webmvc, tomcat, jackson |
| `spring-boot-starter-data-jpa` | JPA 支持 | hibernate, spring-data-jpa |
| `spring-boot-starter-jdbc` | JDBC 支持 | HikariCP, spring-jdbc |
| `spring-boot-starter-test` | 测试支持 | JUnit, Mockito, AssertJ |
| `spring-boot-starter-security` | 安全控制 | spring-security |
| `spring-boot-starter-cache` | 缓存支持 | spring-context-support |
| `spring-boot-starter-actuator` | 监控端点 | micrometer, metrics |
| `spring-boot-starter-logging` | 日志支持 | logback, log4j2 |

### 1.3 Parent POM 版本管理

```xml
<!-- 版本统一管理 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
</parent>

<!-- 不需要指定版本，parent 已管理 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## 2. Actuator 监控

### 2.1 概念

Actuator 提供生产级监控端点，用于查看应用运行状态、指标、健康检查等。

```
引入依赖:
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

默认端点:
/actuator/health          → 健康检查
/actuator/info            → 应用信息
/actuator/metrics         → 运行时指标
/actuator/env             → 环境属性
/actuator/beans           → 所有 Bean
/actuator/loggers         → 日志配置
/actuator/threaddump      → 线程转储
/actuator/heapdump        → 堆转储
```

### 2.2 健康检查

```
健康指示器（HealthIndicator）:

磁盘空间检查:
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 100000000000,
        "threshold": 10485760
      }
    },
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "SELECT 1"
      }
    }
  }
}

自定义健康指示器:
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        int errorCode = check();
        if (errorCode != 0) {
            return Health.down()
                .withDetail("Error Code", errorCode)
                .build();
        }
        return Health.up().build();
    }
}
```

### 2.3 Metrics 指标

```
Micrometer 指标:

计数器（Counter）:
MeterRegistry registry;
Counter counter = Counter.builder("http.requests")
    .tag("method", "GET")
    .tag("uri", "/api/users")
    .register(registry);
counter.increment();

计时器（Timer）:
Timer timer = Timer.builder("http.request.duration")
    .tag("method", "GET")
    .register(registry);
timer.record(() -> {
    // 执行业务逻辑
});

Gauge（仪表盘）:
AtomicInteger activeConnections = new AtomicInteger(0);
Gauge.builder("connections.active", activeConnections, AtomicInteger::get)
    .register(registry);
```

### 2.4 配置安全

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics  # 只暴露必要端点
        exclude: env,beans             # 排除敏感端点
  endpoint:
    health:
      show-details: when_authorized    # 只有认证用户可见详情
    metrics:
      enabled: true
  server:
    port: 8081                         # 独立端口
```

## 3. Profiles 多环境配置

```
多环境配置:

application.yml        # 公共配置
application-dev.yml    # 开发环境
application-test.yml   # 测试环境
application-prod.yml   # 生产环境

激活方式:
1. 配置文件: spring.profiles.active=dev
2. 命令行: java -jar app.jar --spring.profiles.active=prod
3. 环境变量: SPRING_PROFILES_ACTIVE=prod

Profile 条件 Bean:
@Bean
@Profile("dev")
public DataSource devDataSource() {
    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .build();
}

@Bean
@Profile("prod")
public DataSource prodDataSource() {
    return DataSourceBuilder.create()
        .url("jdbc:mysql://prod-server:3306/db")
        .build();
}
```

## 4. 事件监听机制

```
SpringBoot 事件体系:

ApplicationStartingEvent      → 应用启动中
ApplicationEnvironmentPreparedEvent → 环境准备完成
ApplicationPreparedEvent      → 应用准备完成
ApplicationStartedEvent       → 应用已启动
ApplicationReadyEvent         → 应用就绪（可用）
ApplicationFailedEvent        → 应用启动失败

监听器实现:
@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("应用已就绪，开始初始化...");
        // 执行启动后的初始化逻辑
    }
}

或使用 @EventListener:
@Component
public class EventHandler {
    @EventListener
    public void handleReady(ApplicationReadyEvent event) {
        // 处理就绪事件
    }
}
```

## 5. 日志配置

```
SpringBoot 默认使用 Logback:

日志级别（由低到高）:
TRACE < DEBUG < INFO < WARN < ERROR

application.yml 配置:
logging:
  level:
    root: INFO
    org.springframework.web: DEBUG
    com.example: TRACE
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/application.log
    max-size: 10MB
    max-history: 30

logback-spring.xml 高级配置:
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="LOG_PATTERN" 
        value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>
    
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

## 6. 异常处理

```
全局异常处理:

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(500, "系统内部错误"));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(400, message));
    }
}
```

## 最佳实践

1. **合理使用 Starter**，避免引入不必要的依赖
2. **Actuator 端点暴露要谨慎**，生产环境只暴露 health 和 info
3. **敏感端点使用独立端口**，与业务端口分离
4. **自定义 HealthIndicator** 检查关键依赖（数据库、缓存、MQ）
5. **使用 @Profile** 管理多环境配置，不要在代码中硬编码环境判断
6. **日志级别合理配置**，生产环境避免 DEBUG 级别