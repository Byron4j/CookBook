# Hystrix 断路器

在分布式微服务系统中，为了保证高可用，一般都会集群部署实例。
可能由于网络原因、服务自身其它原因导致某个服务出现问题，而使用前面[Ribbon+RestTemplate 负载均衡](2-Ribbon负载均衡.md) 或者 [Feign负载均衡](3-Feign负载均衡.md)来调用这个故障的服务就会出现线程阻塞，此时如果有大量的Servlet容器的线程资源被消耗完毕，导致服务瘫痪不可用。
由于服务之间的依赖性，可能导致故障传播，蔓延到整个为服务系统，造成“雪崩”效应。

断路器是专门为解决此类问题提出的解决方案。

## 断路器简介

>Netflix 创建了一个叫做 ```Hystrix``` 的库，实现了断路模式。在一个微服务架构中，它通常会有多层服务调用。

奈飞开源了Hystrix组件，实现了断路器模式，SpringCloud对这一组建进行了整合。

我们来看一张为服务图：

![](pictures/hystrix/1.png)

### Hystrix 回退可以防止级联崩毁

- 拥有一个开放的电路可以停止级联故障，并允许过载或故障服务有时间恢复；
- 回退可以是另一个受Hystrix保护的调用、静态数据或一个合理的空值；
- 回退可能是链式的，以便第一个回退执行一些其他业务调用，而这些业务调用又返回到静态数据；
- 当对特定的服务的调用的不可用达到一个阀值（Hystric 是5秒20次） 断路器将会被打开；
- 断路打开后，可用避免连锁故障，fallback方法可以直接返回一个固定值。

![](pictures/hystrix/2.png)

## 准备

新建一个工程，也可以在service-ribbon工程中改造。这里我们新建一个工程，专门演示Hystrix的使用。

### 引入依赖

```xml
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
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
<!-- Hystrix仪表盘 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

### 修改配置

```properties


spring.application.name=service-hystrix
# 本实例端口
server.port=8766

# eureka注册中心的配置信息
registry.port=8761
eureka.instance.hostname=localhost
eureka.client.serviceUrl.defaultZone=http://${eureka.instance.hostname}:${registry.port}/eureka/
```


## 编写启动类，开启RestTemplate+Ribbon负载均衡

```java
@SpringBootApplication
@EnableEurekaClient
public class ServiceHystrixApplication {


    @Bean
    @LoadBalanced  // 基于Ribbon+RestTemplate实现负载均衡
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(ServiceHystrixApplication.class, args);
    }



}
```

## 编写服务类

```java
@Service
public class HystrixService {

    @Autowired
    RestTemplate restTemplate;

    public String sayHi(String name){
        return restTemplate.getForObject("http://eureka-srv-cli1?name=" + name, String.class);
    }
}
```

## 编写Controller

```java
@RestController
public class HiController {

    @Autowired
    HystrixService hystrixService;
    @RequestMapping(value = "/hi", method = RequestMethod.GET)
    public String sayHi(@RequestParam String name){
        return hystrixService.sayHi(name);
    }

}

```

## 开启Hystrix功能 ```@EnableHystrix```

在上面的启动类中增加 ```@EnableHystrix``` 开启Hystrix功能。

```java
@SpringBootApplication
@EnableEurekaClient // eureka客户端
@EnableHystrix // 开启Hystrix
public class ServiceHystrixApplication {

    @Bean
    @LoadBalanced  // 基于Ribbon+RestTemplate实现负载均衡
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(ServiceHystrixApplication.class, args);
    }



}
```

## 改造service类，增加 ```@HystrixCommand```注解处理

```java

@Service
public class HystrixService {

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "hiError")  // 指定熔断的回退方法
    public String sayHi(String name){
        return restTemplate.getForObject("http://eureka-srv-cli1/hi?name=" + name, String.class);
    }

    public String hiError(String name){
        return name + ",so sorry, something error.";
    }
}

```

## 测试

启动启动类，访问： localhost:8766?name=hystrix, 浏览器输出：

>hi hystrix,i am from port:8763
>
>hi hystrix,i am from port:8762

关闭两台eureka-srv-cli1实例，再访问： localhost:8766?name=hystrix, 浏览器输出：

>hystrix,so sorry, something error.

说明当eureka-srv-cli1服务不可用的时候，调用eureka-srv-cli1的接口时，会执行快速失败，直接返回一个字符串，而不是等待响应超时，很好的控制了线程阻塞。


## Feign 中使用 Hystrix 断路器

### 打开断路器

Feign 自带断路器，可以在配置文件中打开：

>feign.hystrix.enabled=true

### 程序中使用

#### 修改Feign接口指定fallback属性

只需要在Feign接口的 ```@FeignClient``` 注解属性 ```fallback``` 指定该接口的实现类的class即可：

```java
@FeignClient("eureka-srv-cli1", fallback = FeignServiceHiImpl.class)  // 指定服务应用名
@Service
public interface IFeignServiceHi {

    @RequestMapping(value = "/hi" ,method = RequestMethod.GET)
    String sayHiFromClient(@RequestParam(value = "name") String name);
}
```

#### 编写Feign接口的实现类

```java
@Component
public class FeignServiceHiImpl implements FeignServiceHiImpl {
    @Override
    public String sayHiFromClient(String name) {
        return "sorry "+name;
    }
}


```

#### 测试

测试方式和上面的使用Ribbon+RestTemplate一样的。

 