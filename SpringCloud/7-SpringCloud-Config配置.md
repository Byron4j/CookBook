# SpringCloud Config 配置管理

## 创建server端

新建一个工程，下面创建一个模块：springcloud-config-server


### 引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>

```

### 启动类加上 ```@EnableConfigServer``` 开启配置服务器的功能

```java
@EnableConfigServer // 注解开启配置服务器的功能
@SpringBootApplication
public class SpringcloudConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudConfigServerApplication.class, args);
    }

}
```

### 配置远程git的配置文件

编写 application.properties :

```properties

spring.application.name=config-server
server.port=8888

# 远程git仓库配置地址
spring.cloud.config.server.git.uri=https://github.com/Byron4j/springcloudconfig.git
spring.cloud.config.server.git.searchPaths=respo
spring.cloud.config.label=master
# public仓库可不填
spring.cloud.config.server.git.username=
spring.cloud.config.server.git.password=
```

在https://github.com/Byron4j/springcloudconfig.git这个仓库下面白那些一个配置属性文件： config-client-dev.properties，其内容如下：

```properties
foo = foo version 3
```

### 测试服务端

启动服务，浏览器访问 http://localhost:8888/foo/dev：

>{"name":"foo","profiles":["dev"],"label":null,"version":"b15971f7cd0b650ccb62b0ca9a1113607bce7ede","state":null,"propertySources":[]}


可以看到配置服务中心可以从远程git仓库获取配置属性信息。

访问规则如下：

- /{appication}/{profile}/[{label}]

- /{application}-{profile}.yml

- /{application}-{profile}.properties

- /{label}/{application}-{profile}.properties

- /{label}/{application}-{profile}.yml


## 构建client端（可以认为是我们的应用服务）

创建一个模块： springcloud-config-client。

### 引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

### 编写配置文件bootstrap.yml/bootstrap.properties

编写bootstrap.yml或bootstrap.properties配置文件内容，该文件先于application.properties文件加载。

**bootstrap.properties** 文件内容如下：

```properties
spring.application.name=config-client
server.port=8881 


# 远程仓库的分支
spring.cloud.config.label=master

# 环境； 一般为 dev-开发、test-测试、pro-生产
spring.cloud.config.profile=dev

# 指明配置服务中心的网址即前面的springcloud-config-server实例
spring.cloud.config.uri= http://localhost:8888/

```


### 测试，直接在启动类测试

```java
@RestController
@SpringBootApplication
public class SpringcloudConfigClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudConfigClientApplication.class, args);
    }


    @Value("${foo}")
    String foo;

    @GetMapping("/hi")
    public String hi(){
        return foo;
    }

}

```


启动服务，访问： localhost:8881/hi

>foo version 3

现在形成了：  

>服务应用--------》配置服务---------》远程git配置仓库

的调用链。

#### 参考资料

- [SpringCloud-Config官网API](https://cloud.spring.io/spring-cloud-static/spring-cloud-config/2.1.0.RELEASE/single/spring-cloud-config.html)
- [方志朋的博客](https://www.fangzhipeng.com/springcloud/2018/08/06/sc-f6-config.html)
- [基于 Spring Cloud 的TCC柔性事务](https://github.com/prontera/spring-cloud-rest-tcc)