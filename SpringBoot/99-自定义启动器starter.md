# SpringBoot自定义启动器

一个完整的SpringBoot启动器包含以下几个部分：

- ```autoconfigure``` 模块包含自动配置的代码
- ```starter``` 模块提供自动配置的常用的依赖；简而言之，添加starter应该提供开始使用该库所需的一切。

>你可以在一个模块中组合自动配置的代码和依赖管理。

## 命名

你需要确保为启动器提供一个合适的命名空间。
- 不要使用```spring-boot```开头，这通常是官方预留的。

一个经验法则是，你应该在。
例如，假设你为"acme"创建了一个启动器，你命名自动配置模块为
```acme-spring-boot-autoconfigure```。将启动器命名为```acme-spring-boot-starter```。
如果你有一个模块是由这两个组合的，将其命名为```acme-spring-boot-starter```。

同样，如果你的启动器提供了配置keys，给他们使用唯一的命名空间。
特别的，不要在你的keys中包含Spring Boot使用的诸如：```server```、```management```、```spring```等等。
如果你是用相同的命名空间，将来可能以破坏模式的方式修改这些命名空间。

确保[触发元数据生成](https://docs.spring.io/spring-boot/docs/2.1.5.RELEASE/reference/htmlsingle/#configuration-metadata-annotation-processor)，以便可以使用IDE帮助。你可能想检阅一下生成的元数据（```META-INF/spring-configuration-metadata.json```）以确保你的keys被合适地文档化了。

## ```autoconfigure``` 模块

```autoconfigure``` 模块包含了任何你需要用到的。也可能包含配置key的定义（例如```@ConfigurationProperties```）和任何回调接口（用于未来自定义怎样初始化组件）。

>您应该将库的依赖项标记为可选的，这样就可以更容易地在项目中包含autoconfigure模块。

Spring Boot 使用一个注解处理器收集在元数据文件```META-INF/spring-autoconfigure-metadata.properties```符合自动配置的条件。
如果该文件存在的话，则使用它过滤不匹配的自动配置，这会加快启动时间。

推荐在包含自动配置的模块中使用以下依赖：

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-autoconfigure-processor</artifactId>
	<optional>true</optional>
</dependency>
```


## 参考开源实战

这里我们选择一个github开源的集成swagger的项目：
```xml
<groupId>com.spring4all</groupId>
<artifactId>swagger-spring-boot-starter</artifactId>
<version>1.9.0.RELEASE</version>
```

可以看到项目组成比较简单：

![](SpringBoot/pictures/1-starter/1.png)


### 步骤

- 创建springboot项目
- 引入需要依赖的坐标
- 编写一个获取配置属性的类```com.spring4all.swagger.SwaggerProperties```
    -   ```java
            @ConfigurationProperties("swagger")
            public class SwaggerProperties {
                ...
            }
        ```
- 编写配置类```com.spring4all.swagger.SwaggerAutoConfiguration```
    -   ```java
            @Configuration
            @Import({Swagger2Configuration.class})
            public class SwaggerAutoConfiguration implements BeanFactoryAware {
                ...
            }
        ```
        
- 编写```META-INF/spring-configuration-metadata.json``` 文件



#### 参考资料

- [SpringBoot自定义启动器](https://docs.spring.io/spring-boot/docs/2.1.5.RELEASE/reference/htmlsingle/#boot-features-custom-starter)
- [简单示例](https://www.cnblogs.com/cz-xjw/p/6632402.html)
- [Mybatis-starter](http://www.importnew.com/24164.html)