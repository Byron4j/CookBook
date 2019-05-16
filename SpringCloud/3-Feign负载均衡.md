# Feign 负载均衡

在[Ribbon负载均衡](2-Ribbon负载均衡.md)中介绍了 RestTemplate+Ribbon 的负载均衡实现，还有另一种基于注解的Feign负载均衡实现，我们来了解一下。


## Feign 简介

- Feign 是一个声明式的伪 Http 客户端。使用 Feign 时，仅仅需要创建一个接口并注解。
- Feign 支持可插拔的编码器、解码器。
- Feign 默认集成了 Ribbon，并和 Eureka 结合，实现了默认的负载均衡功能。
- 整合了 Hystrix，具有熔断能力。


## 准备

继续启用 eureka-srv 端口为 8761、eureka-srv-cli1 两个实例 端口为 8762、8763.

## 创建一个 Feign 服务

工程为 service-feign，引入依赖：

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
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

## 修改配置信息


配置文件中指定服务名伪： service-feign， 端口伪 8765， 注册到注册中心： http://localhost:8761/eureka/。
application.properties:

```properties
spring.application.name=service-feign
# 本实例端口
server.port=8765

# eureka注册中心的配置信息
registry.port=8761
eureka.instance.hostname=localhost
eureka.client.serviceUrl.defaultZone=http://${eureka.instance.hostname}:${registry.port}/eureka/
```


## 修改启动类，添加注解 ```@EnableFeignClients```

```java
@EnableFeignClients  // 开启Feign的功能
@EnableDiscoveryClient
@SpringBootApplication
public class ServiceFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceFeignApplication.class, args);
    }

}
```

## 定义一个Feign接口

定义一个接口使用，使用 ```@ FeignClient("服务名"")```，来指定调用哪个服务。
比如，我们调用 eureka-srv-cli1 应用的 /hi 接口，代码示例如下：

```java
@FeignClient("eureka-srv-cli1")  // 指定服务应用名
@Service
public interface IFeignServiceHi {

    @RequestMapping(value = "/hi" ,method = RequestMethod.GET)
    String sayHiFromClient(@RequestParam(value = "name") String name);
}
```

## 向外层暴露一个Controller，调用Feigb接口定义的服务来消费

```java

@RestController
public class HiController {

    @Autowired
    IFeignServiceHi iFeignServiceHi;

    @GetMapping("/hi")
    public String sayHi(@RequestParam String name){
        return iFeignServiceHi.sayHiFromClient(name);
    }
}
```



## 测试

当然，测试之前，请先关闭[Ribbon](2-Ribbon负载均衡.md)实例，以免干扰；
再启动本例的Feign启动类，然后访问：  ```http://localhost:8765/hi?name=forezp```，也看到了和Ribbon负载均衡一样的效果：

>hi forezp,i am from port:8763
>
>hi forezp,i am from port:8762
>
>hi forezp,i am from port:8763
>
>hi forezp,i am from port:8762


## 总结

- 引入依赖: ```spring-cloud-starter-openfeign```

- 启用Feign注解:  在启动类中使用 ```@EnableFeignClients``` 注解开启Feign的功能

- 编写Feign接口： 编写接口，并使用 ```@FeignClient("eureka-srv-cli1")```  用于指定需要进行负载均衡的服务应用名

#### 参考资料

- [方志朋的博客](https://www.fangzhipeng.com/springcloud/2018/08/03/sc-f3-feign.html)
- [跳出SpringCloud单独使用Feign](https://www.jianshu.com/p/3d597e9d2d67)