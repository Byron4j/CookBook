# [SpringCloud Bus 消息总线（官网翻译）](https://cloud.spring.io/spring-cloud-static/spring-cloud-bus/2.1.0.RELEASE/single/spring-cloud-bus.html)

Spring Cloud Bus使用轻量级消息代理将分布式系统的节点链接起来。然后，可以使用此代理广播状态更改(例如配置更改)或其他管理指令。

一个关键的思想是消息总线好比一个SpringBoot中的分布式的actuator，可扩展。但是，它也可以作为应用之间通讯的方式。


## 快速开始

Spring Cloud Bus消息总线通过classpath下添加的SpringBoot配置来发现自己。
为了使得总线可用，需要添加依赖：```spring-cloud-starter-bus-amqp``` 或者 ```spring-cloud-starter-bus-kafka```。SpringCloud会处理剩下的工作。确保消息代理（RabbitMQ、Kafka）是配置可用的。

当在本地运行的时候，你不需要做任何事情。如果你在远程运行，使用SpringCloud连接器或者SpringBoot约定定义代理凭据，像以下Rabbit示例一样：

**application.yml**:

```yaml
spring:
    rabbitmq:
      host: mybroker.com
      port: 5672
      username: user
      password: secret
```

消息总线目前支持将消息发送到正在侦听某个特定服务的所有节点或所有节点(由Eureka定义)。
```/bus/*``` 执行器命名空间有一些http端点。现在，实现了两个。
第一个是 ```/bus/env``` ，发送 key/value 对来更新每个节点的Spring环境。
第二个是 ```/bus/refresh``` ，重新加载每个应用的配置，因为它们在之前已经连通了```/refresh```端点。


>SpringCloud Bus消息总线通过```RabbitMQ```和```Kafka```启动，因为这两者是最常用的实现（可以考虑使用```RocketMQ```实现）。但是，SpringCloud Stream是非常灵活的，并且绑定器使用的是```spring-cloud-bus```。


## 总线端点

Spring Cloud Bus 提供了2个端点： ```/actuator/bus-refresh``` 和 ```/actuator/bus-env``` ，它们对应SpringCloud Commons 的```/actuator/refresh```、```/actuator/env```单个制动改动点。

### 总线刷新端点 Bus Refresh Endpoint

```/actuator/bus-refresh``` 端点清除 ```RefreshScope``` 缓存并重新绑定 ```@ConfigurationProperties```。

为了暴露 ```/actuator/bus-refresh``` 端点，你需要添加以下内容到配置文件中：

```properties
management.endpoints.web.exposure.include=bus-refresh
```
 
 
### 总线环境端点 Bus Env Endpoint

```/actuator/bus-env``` 端点跨实例使用指定的 ```key/value``` 对更新每个实例的环境。

为了暴露 ```/actuator/bus-env``` 端点，需要添加以下内容到配置文件:

```properties
management.endpoints.web.exposure.include=bus-env
```

```/actuator/bus-env``` 端点接收以下形式的 ```POST``` 请求：

```json
{
	"name": "key1",
	"value": "value1"
}
```

## 寻址一个实例

每个应用的实例都有一个 service ID， 可以通过 ```spring.cloud.bus.id``` 设置其值，值为冒号分隔的多个形式。
默认值是是由 ```spring.application.name``` 、```server.port```(或者 ```spring.application.index```如果设置了的话) 组合而成的。
默认id形式是： ```app:index:id``` :

- ```app``` 是 ```spring.application.name``` 指定的
- ```index``` 是 ```spring.application.index```、```local.server.port```、```server.port```或者```0```指定的
- ```id``` 是```vcap.application.instance_id```，如果存在的话； 或者是一个随机值

HTTP 端点接收一个 "目的地destination" path 参数，例如 ```/bus-refresh/cunstomers:9000```，在这里 ```destination``` 是一个service ID。如果这个ID是被总线上的某个实例所有，则它将处理该消息，而所有其他实例将忽略该消息。

## 在一个服务上寻址所有的实例

前面的 ```destination``` 参数用于一个Spring ```PatchMatcher```(path由```:```分隔) 去决定是否某个实例去处理消息。
使用实例， ```/bus-env/cunstomers:**``` 目标即所有的 customers 服务实例。

## 服务ID必须是唯一的

总线会尝试2次去消除对事件的处理————一次从源```ApplicationEvent``` ，另一次是从队列中。
为此，总线根据当前服务ID检查发送服务ID。如果一个服务的多个实例拥有的是相同的服务ID，事件则不会处理。
当在本地机器运行时，每个服务都是在不同的端口，端口是服务ID的一部分。云计算提供了一个指标来区分。
为了确保服务ID在云计算之外是唯一的，需要为服务的每个实例都设置```spring.application.index```。

## 自定义消息代理

Spring Cloud Bus 使用 Spring Cloud Stream 去广播消息。所以，要让消息流动起来，你需要的仅仅是在你的classpath中包含你选择的绑定器实现。
对于RabbitMQ和Kafka则有一个方便的启动器```spring-cloud-starter-bus-[amqp|kafka]``` 。
一般而言，Spring Cloud Stream 依靠 Spring Boot 的自动配置可以很方便的配置中间件。
举个例子，AMQP 代理寻址可以通过改变```spring.rabbitmq.*```配置属性来达到目的。
Spring Cloud Bus 在```spring.cloud.bus.*``` 中有一些本地属性，例如```spring.cloud.bus.destination```是使用外部中间件的topic的name。通常情况下，默认的已经足够使用了。

可以查看 Spring Cloud Stream 文档可以获得更多的自定义消息代理设置。

## 跟踪总线事件Bus Events

总线事件（也是```RemoteApplicationEvent``` 的子类）可以通过```spring.cloud.bus.trace.enabled=true```来设置。
如果设置了，SprinigBoot的```TraceRepository```(如果存在的话)会显示每个发送过的事件和每个服务实例的响应。以下是一个来自```/trace```端点的示例：

```json
{
  "timestamp": "2015-11-26T10:24:44.411+0000",
  "info": {
    "signal": "spring.cloud.bus.ack",
    "type": "RefreshRemoteApplicationEvent",
    "id": "c4d374b7-58ea-4928-a312-31984def293b",
    "origin": "stores:8081",
    "destination": "*:**"
  }
  },
  {
  "timestamp": "2015-11-26T10:24:41.864+0000",
  "info": {
    "signal": "spring.cloud.bus.sent",
    "type": "RefreshRemoteApplicationEvent",
    "id": "c4d374b7-58ea-4928-a312-31984def293b",
    "origin": "customers:9000",
    "destination": "*:**"
  }
  },
  {
  "timestamp": "2015-11-26T10:24:41.862+0000",
  "info": {
    "signal": "spring.cloud.bus.ack",
    "type": "RefreshRemoteApplicationEvent",
    "id": "c4d374b7-58ea-4928-a312-31984def293b",
    "origin": "customers:9000",
    "destination": "*:**"
  }
}
```

追踪情况显示了从```customers:9000```发送了一个```RefreshRemoteApplicationEvent```事件，并且广播到了所有的服务中，被```customers:9000```和```tores:8081```接收确认。

为了你自己解决确认信号，你需要添加一个```@EventListener```注解到你的应用的```AckRemoteApplicationEvent```和```SentApplicationEvent```类中以便跟踪。可选的方案是，你可以从```TraceRepository```获取数据。

>任何Bus应用都可以跟踪acks。但是，有时候，在一个中心服务中是很有用处的。它可以做更多的复杂查询，或者将数据转发给指定的跟踪服务去处理。

## 广播你自己的事件

Bus总线可以携带任何```RemoteApplicationEvent```类型的事件。默认的传输形式是 JSON，反序列化器需要提前知道是哪种类型。
如果要注册一个新类型，你必须将它放进```org.springframework.cloud.bus.event```的子包下。

自定义事件名称的话，你需要在你的自定义类中添加```@JsonTypeName```注解，或者依靠默认的策略————使用class的简单名称(simple name)。

>生产者、消费者需要访问类的定义

### 在自定义包中注册事件

如果你不能或者不想在```org.springframework.cloud.bus.event```子包下自定义事件，你必须通过```@RemoteApplicationEventScan```注解标记扫描```RemoteApplicationEvent```类型的事件类。```@RemoteApplicationEventScan``` 也包含了子包。

示例，自定义事件通过继承```RemoteApplicationEvent```：

```java
package com.acme;

public class MyEvent extends RemoteApplicationEvent {
    ...
}
```

你可以按以下方式将事件注册到反序列化器中：
```java
package com.acme;

@Configuration
@RemoteApplicationEventScan
public class BusConfiguration {
    ...
}
```

在不指定值的情况下，注册使用```@RemoteApplicationEventScan```的类的包,这里注册了```com.acme```包及其子包；

你也可以通过在```@RemoteApplicationEventScan```注解上添加```value```、```basePackages```或者```basePackageClasses```属性来指定扫描包，例如：
```java
package com.acme;

@Configuration
//@RemoteApplicationEventScan({"com.acme", "foo.bar"})
//@RemoteApplicationEventScan(basePackages = {"com.acme", "foo.bar", "fizz.buzz"})
@RemoteApplicationEventScan(basePackageClasses = BusConfiguration.class)
public class BusConfiguration {
    ...
}
```

## 使用RabbitMQ实战Spring Cloud Bus消息总线

- 需要在配置文件中配置```spring-cloud-starter-bus-amqp```；
- 需要依赖RabbitMQ，可以参考[RabbitMQ系列](https://blog.csdn.net/zixiao217/article/category/6428331)

### 增加pom依赖

```xml
<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-config</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-bus-amqp</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
</dependencies>

```

### application.properties配置RabbitMQ信息、消息总线信息

```properties
# RabbitMQ相关配置信息
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# 总线配置
spring.cloud.bus.enabled=true
# 允许跟踪
spring.cloud.bus.trace.enabled=true
# 暴露端点
management.endpoints.web.exposure.include=bus-refresh
```

### 启动类编写

```java
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@RestController
@RefreshScope  // /actuator/bus-refresh 端点清除 RefreshScope 缓存并重新绑定 ```@ConfigurationProperties```
public class ConfigClientApplication {

	/**
	 * http://localhost:8881/actuator/bus-refresh
	 */

	public static void main(String[] args) {
		SpringApplication.run(ConfigClientApplication.class, args);
	}

	@Value("${foo}")
	String foo;

	@RequestMapping(value = "/hi")
	public String hi(){
		return foo;
	}
}
```

### 测试消息总线实例

依次启动eureka-server、confg-cserver,启动两个config-client，端口为：8881、8882。
访问http://localhost:8881/hi 或者http://localhost:8882/hi 浏览器显示：
>foo version 3

>这时我们去代码仓库将foo的值改为```foo version 4```，即改变配置文件foo的值。如果是传统的做法，需要重启服务，才能达到配置文件的更新。
>
>此时，我们只需要发送post请求：http://localhost:8881/actuator/bus-refresh，你会发现config-client会重新读取配置文件。

这时我们再访问http://localhost:8881/hi 或者http://localhost:8882/hi 浏览器显示：
>foo version 4

另外，/actuator/bus-refresh接口可以指定服务，即使用前面提到的```destination```参数，比如 `````/actuator/bus-refresh?destination=customers:**````` 即刷新服务名为customers的所有服务。


当git文件更改的时候，通过pc端用post 向端口为8882的config-client发送请求/bus/refresh/；此时8882端口会发送一个消息，由消息总线向其他服务传递，从而使整个微服务集群都达到更新配置文件。

#### 参考资料

- [SpringCloud Bus 消息总线（官网）](https://cloud.spring.io/spring-cloud-static/spring-cloud-bus/2.1.0.RELEASE/single/spring-cloud-bus.html)

- [方志朋的博客](https://www.fangzhipeng.com/springcloud/2018/08/08/sc-f8-bus.html)