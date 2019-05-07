# 整合Swagger

## 借鉴

借鉴开源项目 [com.spring4all.swagger-spring-boot-starter](https://github.com/SpringForAll/spring-boot-starter-swagger)。

## 依赖

```xml
<dependency>
	<groupId>com.spring4all</groupId>
	<artifactId>swagger-spring-boot-starter</artifactId>
	<version>1.9.0.RELEASE</version>
</dependency>
```


## 使用步骤

### 在启动类中添加```@EnableSwagger2Doc```注解

```java
@EnableSwagger2Doc
@SpringBootApplication
public class Bootstrap {

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }

}
```

默认情况下就能产生所有当前Spring MVC加载的请求映射文档。

### 编写Controller

```java
@RestController("hello")
public class HelloController {
    @ApiOperation(value = "Hello World", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello(){
        
    }
}

```


### 访问swagger-ui界面

http://localhost:8080/swagger-ui.html/


#### 参考资料

- [https://blog.csdn.net/u010919351/article/details/70105863](https://blog.csdn.net/u010919351/article/details/70105863)
- [https://github.com/SpringForAll/spring-boot-starter-swagger](https://github.com/SpringForAll/spring-boot-starter-swagger)

