# SpringBoot自动装配原理

## 1. 概念

SpringBoot 自动装配（Auto-Configuration）是其最核心的特性之一。它根据类路径中的依赖、配置属性、条件注解，自动配置 Spring 应用所需的 Bean，极大简化了开发配置。

```
传统 Spring  vs  SpringBoot:

传统 Spring:
┌─────────────────────────────────────────┐
│  applicationContext.xml                 │
│  <bean id="dataSource" ...>            │
│  <bean id="transactionManager" ...>    │
│  <bean id="sqlSessionFactory" ...>     │
│  数十行 XML 配置...                      │
└─────────────────────────────────────────┘
            ↓
        手动配置每一个 Bean

SpringBoot:
┌─────────────────────────────────────────┐
│  pom.xml                                │
│  <dependency>                          │
│    <groupId>mybatis-spring-boot</groupId>│
│  </dependency>                         │
└─────────────────────────────────────────┘
            ↓
        自动配置完成！
        DataSource、TransactionManager、
        SqlSessionFactory 全部自动注入
```

## 2. 来源

SpringBoot 1.0（2014年）引入自动装配，解决 Spring 配置繁琐的痛点。核心理念：**约定优于配置**（Convention Over Configuration）。

## 3. 知识点

### 3.1 入口：@SpringBootApplication

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@SpringBootApplication = 
    @Configuration + 
    @EnableAutoConfiguration + 
    @ComponentScan

@ComponentScan: 扫描当前包及子包下的组件
@Configuration: 标识配置类
@EnableAutoConfiguration: 开启自动装配（核心）
```

### 3.2 @EnableAutoConfiguration 原理

```
@EnableAutoConfiguration 导入了一个关键类：
@Import(AutoConfigurationImportSelector.class)

AutoConfigurationImportSelector 的核心流程：

1. 读取 META-INF/spring.factories
   位置: spring-boot-autoconfigure.jar
   
   # Auto Configure
   org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
   org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
   org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,\
   org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration,\
   ...

2. 过滤：根据条件注解 @Conditional 判断是否需要装配
   - @ConditionalOnClass: 类路径存在某类
   - @ConditionalOnMissingBean: 容器中不存在某 Bean
   - @ConditionalOnProperty: 配置属性满足条件

3. 注册：将满足条件的配置类注册到 Spring 容器
```

### 3.3 条件注解详解

```
条件注解体系:

@ConditionalOnClass(DataSource.class)
    → 类路径存在 DataSource 才装配
    
@ConditionalOnMissingBean(DataSource.class)
    → 用户未自定义 DataSource 才使用默认
    
@ConditionalOnProperty(
    prefix="spring.datasource", 
    name="url"
)
    → 配置了 spring.datasource.url 才装配

@ConditionalOnWebApplication
    → 是 Web 应用才装配

装配顺序控制:
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
    → 在 DataSource 装配之后再装配
    
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
    → 在 WebMvc 之前装配
```

### 3.4 自动装配源码分析

```java
// DataSourceAutoConfiguration 示例
@Configuration
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@EnableConfigurationProperties(DataSourceProperties.class)
@Import({ DataSourcePoolMetadataProvidersConfiguration.class,
        DataSourceInitializationConfiguration.class })
public class DataSourceAutoConfiguration {

    @Configuration
    @ConditionalOnClass(HikariDataSource.class)  // 类路径有 HikariCP
    @ConditionalOnMissingBean(DataSource.class)   // 用户未定义 DataSource
    @ConditionalOnProperty(                       // 配置满足条件
        name = "spring.datasource.type",
        havingValue = "com.zaxxer.hikari.HikariDataSource",
        matchIfMissing = true
    )
    static class Hikari {
        @Bean
        @ConfigurationProperties(prefix = "spring.datasource.hikari")
        public HikariDataSource dataSource(DataSourceProperties properties) {
            // 创建并配置 HikariDataSource
            return createDataSource(properties, HikariDataSource.class);
        }
    }
}
```

### 3.5 配置属性绑定

```
@ConfigurationProperties 机制:

application.yml:
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver

对应类:
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    // getters/setters
}

绑定流程:
1. @EnableConfigurationProperties 导入配置属性类
2. ConfigurationPropertiesBindingPostProcessor 处理绑定
3. 将配置文件中的属性值绑定到 Java 对象的字段
```

## 4. 自定义 Starter

```
自定义 Starter 结构:

my-spring-boot-starter/
├── src/main/java/
│   └── com/example/autoconfigure/
│       ├── MyAutoConfiguration.java      # 自动配置类
│       └── MyProperties.java             # 配置属性类
├── src/main/resources/
│   └── META-INF/
│       └── spring.factories              # 注册自动配置
└── pom.xml

spring.factories:
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.autoconfigure.MyAutoConfiguration

MyAutoConfiguration.java:
@Configuration
@ConditionalOnClass(MyService.class)
@EnableConfigurationProperties(MyProperties.class)
public class MyAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public MyService myService(MyProperties properties) {
        return new MyService(properties.getName());
    }
}
```

## 5. 最佳实践

1. **排除不需要的自动配置**：`@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)`
2. **自定义配置覆盖默认**：定义同名 Bean 即可
3. **使用 @Conditional 系列注解** 控制装配条件
4. **配置属性使用 relaxed binding**：`spring.datasource.url` = `SPRING_DATASOURCE_URL`
5. **查看自动装配报告**：`--debug` 或 `logging.level.org.springframework.boot.autoconfigure=DEBUG`

## 运行验证

```bash
./mvnw clean compile
# 查看自动装配报告
java -jar target/*.jar --debug
```