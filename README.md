# JAVA高级架构师技术栈

![GitHub Release](https://img.shields.io/github/v/release/Byron4j/CookBook)
![GitHub Stars](https://img.shields.io/github/stars/Byron4j/CookBook)
![License](https://img.shields.io/github/license/Byron4j/CookBook)
![Last Commit](https://img.shields.io/github/last-commit/Byron4j/CookBook)
![](https://img.shields.io/badge/author-%E4%B8%9C%E9%99%86%E4%B9%8B%E6%BB%87-red.svg)

>任何技能通过 “刻意练习” 都可以达到融会贯通的境界，就像烹饪一样，这里有一份JAVA开发技术手册，只需要增加自己练习的次数。

## 项目总览（作者与读者共用）

`CookBook` 是一个以 `Java` 技术栈为核心的知识仓库，采用“知识文档 + 示例代码”双轨组织方式：

- 知识文档层：按技术域沉淀专题内容（如 `Java核心`、`JVM`、`Spring`、`SpringCloud`、`架构`、`分布式高并发`）。
- 示例代码层：在 `src/main/java/org/byron4j/cookbook` 中提供可运行、可阅读的实践示例（如 Netty、RocketMQ、Zookeeper、设计模式等）。
- 工程化层：基于 `Maven` 构建（`pom.xml`、`mvnw`），配有测试与 CI 入口（`src/test`、`.travis.yml`）。

本仓库既适合作为个人/团队技术知识地图，也适合作为分层进阶学习路线图。

## 学习深度分层（基础 → 中级 → 高级 → 资深专家）

### 1) 基础层
目标：夯实 Java 基础语法、常用机制与基础框架认知。

- 推荐先读：`Java核心`（枚举、注解、反射、动态代理）
- 配套基础：`数据结构和算法`、`Spring/1-IOC相关.md`
- 产出能力：能独立完成常规业务编码与基础问题排查

### 2) 中级层
目标：形成并发、JVM、数据库与主流框架的系统理解。

- 推荐重点：`Java核心/线程池`、`JVM`、`SpringAOP`、`Spring事务`、`MyBatis`、`Redis`
- 代码建议：阅读 `src/main/java/org/byron4j/cookbook/javacore`
- 产出能力：能负责模块设计、性能基础优化与常见故障定位

### 3) 高级层
目标：掌握高并发与分布式系统核心组件及治理能力。

- 推荐重点：`分布式高并发`、`SpringCloud`、`RocketMQ`、`Nginx`、`Zookeeper`
- 代码建议：阅读 `src/main/java/org/byron4j/cookbook/netty`、`rocketmq`、`zk`
- 产出能力：能完成服务治理、限流降级、消息异步化与高可用设计

### 4) 资深专家层
目标：建立架构方法论与源码级分析、性能调优与工程治理能力。

- 推荐重点：`架构/内容/架构内容.md`、`架构/高可用`、`架构/云原生`、`RocketMQ源码片段阅读`、`javassist指南`
- 综合能力：架构权衡、容量评估、可观测性建设、复杂问题根因分析
- 作者向建议：持续维护目录一致性与版本化标记，保持“知识图谱 + 示例代码”同步演进

## 快速导航建议

- 新读者：按“基础 → 中级 → 高级 → 资深专家”顺序阅读。
- 作者/维护者：优先维护 `README` 导航与目录一致性，减少失效链接与重复入口。
- 进阶读者：文档学习与 `src` 示例代码并行阅读，优先关注 Netty、RocketMQ、Zookeeper 三类链路示例。

## 推荐阅读顺序（可跳转）

### 路线 A：基础到中级
1. [数据结构和算法](#topic-algorithm)
2. [JAVA核心高级知识点](#topic-java-core)
3. [Spring](#topic-spring)
4. [MySQL](#topic-mysql)
5. [Redis](#topic-redis)

### 路线 B：中级到高级
1. [JVM](#topic-jvm)
2. [SpringCloud](#topic-springcloud)
3. [RocketMQ](#topic-rocketmq)
4. [NIO/Netty](#topic-netty)
5. [Zookeeper](#topic-zookeeper)

### 路线 C：高级到资深专家
1. [架构](#topic-architecture)
2. [Nginx](#topic-nginx)
3. [Maven](#topic-maven)
4. [Javassist](#topic-javassist)
5. [设计模式](#topic-design-pattern)

<font color=blue size=3>[JAVA程序猿笔试面试干货分享](https://github.com/Byron4j/depthgoods)</font>



**[IDEA-2019-1永久激活教程](软件激活/IDEA-2019-1永久激活教程.md)**

:bowtie:     **觉得不错，就 ★Star 吧**



## **刷题吧**

- [LeetCode刷题仓库](https://github.com/Byron4j/leetcode)

<a id="topic-algorithm"></a>
## **数据结构和算法**


- 数据结构
	- :tada:[01-线性表](数据结构和算法/数据结构/01-线性表.md)
	- :tada:[02-链表](数据结构和算法/数据结构/02-链表.md)
	- :tada:[03-栈、队列](数据结构和算法/数据结构/03-栈、队列.md)
	- :tada:[04-哈希](数据结构和算法/数据结构/04-哈希.md)
	- :tada:[05-树](数据结构和算法/数据结构/05-树.md)
	- :tada:[06-二叉查找树](数据结构和算法/数据结构/06-二叉查找树.md)
	- :tada:[07-红黑树](数据结构和算法/数据结构/07-红黑树.md)
	- :tada:[08-AVL树](数据结构和算法/数据结构/08-AVL树.md)
	- :tada:[09-B树](数据结构和算法/数据结构/09-B树.md)
- 算法
  	- :tada:[快速排序](数据结构和算法/算法/1-快速排序.md)

## **Git**

- [基于Github从零开始搭建个人博客(上)](Git/0-基于Github从零开始搭建个人博客.md)
- [基于Github从零开始搭建个人博客(下)](Git/0-基于Github从零开始搭建个人博客%28下%29.md)
  - [博客](https://zyt505050.gitee.io/2019/01/28/ji-yu-github-hexo-cong-ling-kai-shi-da-jian-ge-ren-bo-ke-xia/)
- :sparkles:[Git提交添加emoji图标](Git/gitCookbook/1-Git提交添加emoji图标.md)
- [Github给项目添加徽标](Git/2-github-travis-ci.md)
- 😆[更多github emoji徽标](Git/gitemojiall.md)

<a id="topic-javassist"></a>
## **Javassist**

- 👽[javassist官网](http://www.javassist.org/)：[官方指南](http://www.javassist.org/tutorial/tutorial.html)
- 👽[javaagent编程指南](javassist指南/0-javassist编程指南概览.md)
- 👽1.[读、写字节码](javassist指南/1-读、写字节码.md)
- 👽2.[ClassPool类池](javassist指南/2-ClassPool.md)
- 👽3.[ClassLoader类加载](javassist指南/3-ClassLoader.md)
- 👽4.[Introspection and customization自省和定制](javassist指南/4-自省和定制.md)

## **JAVA面试汇总**

- 1.:tada:[面试常问题目](面试汇总/1-面试常问题目.md)

<a id="topic-java-core"></a>
## **JAVA核心高级知识点**

### 🌰**SE专题**

- 1.[Java枚举](Java核心/1-Java枚举.md)
- 2.[Java注解](Java核心/2-Java注解.md)
- 3.[Java反射](Java核心/3-Java反射.md)
- 4.[动态代理](Java核心/4-动态代理.md)
- 5.[线程池](Java核心/5-线程池.md)

### 🌰**并发专题**

- 🌰**并发源码解读类**
    - 1.AQS: ```AbstractQueuedSynchronizer```
    - 2.重入锁：```ReentrantLock```

## **XML**

- [1-XML介绍](XML/1-XML介绍.md)
- [2-dom4j解析XML](XML/2-dom4j解析XML.md)
- [3-XPath解析XML](XML/3-XPath解析XML.md)

<a id="topic-mysql"></a>
## **MySQL**

- 🌊.[MySql存储引擎](MySQL/2-MySql存储引擎.md)
- 🌊.[InnoDB存储引擎](MySQL/3-InnoDB存储引擎.md)
- 🌊.[MySQL数据库锁、事务隔离级别详解](MySQL/1-MySQL数据库读写锁示例详解、事务隔离级别示例详解.md)
- 🌊.[Mysql主从复制](MySQL/999-Mysql主从复制.md)
- 🌊.MySQL索引
- 🌊.MySQL优化篇
  - [MySQL数据库的索引原理、与慢SQL优化的5大原则](https://yq.aliyun.com/articles/661447?spm=a2c4e.11153940.0.0.527d158fbcXSEn)
- 🌊.[CentOS7安装MariaDB的流程步骤](MySQL/007-CentOS7安装MariaDB的流程步骤.md)

<a id="topic-spring"></a>
## **Spring**

- 🚻[Spring-IOC](Spring/1-IOC相关.md)
- 🚻[SpringMVC原理解析](Spring/2-SpringMVC原理解析.md)
- 🚻[SpringAOP](Spring/3-SpringAOP.md)
- 🚻[Spring过滤器](Spring/4-过滤器.md)
- 🚻[Spring拦截器](Spring/5-拦截器.md)
- 🚻[Spring事务](Spring/6-Spring事务.md)

## **SpringBoot**

- 🍂[自定义启动器starter](SpringBoot/99-自定义启动器starter.md)

<a id="topic-springcloud"></a>
## **SpringCloud**

- 🐯[0-SpringCloud资料链接](SpringCloud/0-SpringCloud资料链接.md)
- 🐯[1-Eureka服务注册发现](SpringCloud/1-Eureka服务注册发现.md)
- 🐯[2-Ribbon负载均衡](SpringCloud/2-Ribbon负载均衡.md)
- 🐯[3-Feign负载均衡](SpringCloud/3-Feign负载均衡.md)
- 🐯[4-Hystrix熔断器](SpringCloud/4-Hystrix熔断器.md)
- 🐯[5-HystrixDashboard仪表盘](SpringCloud/5-HystrixDashboard仪表盘.md)
- 🐯[6-Zuul动态路由转发-过滤](SpringCloud/6-Zuul动态路由转发-过滤.md)
- 🐯[7-SpringCloud-Config配置](SpringCloud/7-SpringCloud-Config配置.md)
- 🐯[8-SpringCloud-Config高可用架构](SpringCloud/8-SpringCloud-Config高可用架构.md)
- 🐯[9-SpringCloud-Bus消息总线](SpringCloud/9-SpringCloud-Bus消息总线.md)
- 🐯[999-附加参考资料](SpringCloud/999-附加参考资料.md)  

<a id="topic-nginx"></a>
## **Nginx**

- 🚀[Windows环境Ngin](Nginx/1-Windows环境Ngin.md)
- 🚀[Nginx基础入门](Nginx/2-Nginx基础入门.md)
- 🚀[Nginx服务端404以及502等页面配置](Nginx/3-nginx服务端404以及502等页面配置.md)
- 🚀[负载均衡实现实践](Nginx/7-负载均衡实现实践.md)
- 🚀[Nginx实现虚拟主机、反向代理、负载均衡、高可用](Nginx/007-Nginx实现虚拟主机、反向代理、负载均衡、高可用.md)
- 🚀**[Keepalived+Nginx搭建高可用服务](Nginx/007-Nginx实现虚拟主机、反向代理、负载均衡、高可用.md)**
- 🚀**[Nginx配置文件nginx.conf全解](Nginx/9-Nginx配置文件nginx.conf详细介绍.md)**
- 🚀**[如何给nginx添加外部模块](Nginx/10-如何给nginx添加外部模块.md)**
- 🚀[资料分享](Nginx/999-资料分享.md)

<a id="topic-maven"></a>
## **Maven**

- 🐶[编写一个Maven插件](Maven/1-编写一个Maven插件.md)
- [Maven-profile配置](Maven/2-Maven-profile配置.md)

## **Jenkins**

<a id="topic-design-pattern"></a>
## **设计模式**

- 🏦1.[单例模式](设计模式/1-单例模式/1-单例模式.md)
- 🏦2.[观察者模式](设计模式/2-观察者模式/2-观察者模式.md)
- 🏦3.[适配器模式](设计模式/3-适配器模式/3-适配器模式.md)
- 🏦4.[原型模式](设计模式/4-原型模式/4-原型模式.md)
- 🏦5.[建造者模式](设计模式/5-建造者模式/5-建造者模式.md)
- 🏦6.[工厂方法、抽象工厂模式](设计模式/6-工厂方法模式/6-工厂方法模式.md)
- 🏦7.[模板模式](设计模式/7-模板方法/7-模板模式.md)
- 🏦8.[状态模式](设计模式/8-状态模式/8-状态模式.md)
- 🏦9.[策略模式](设计模式/9-策略模式/9-策略模式.md)
- 🏦10.[对象池模式](设计模式/10-对象池设计模式/10-对象池设计模式.md)
- 🏦11.[责任链模式](设计模式/11-责任链模式/11-责任链模式.md)
- 🏦12.[装饰器模式](设计模式/12-装饰器模式/12-装饰器模式.md)

## **MyBatis**

- 🍁[Mybatis面试题](MyBatis/0-Mybatis面试题.md)
- 🍁[传统JDBC弊端与MyBatis的优点](MyBatis/1-传统JDBC弊端与MyBatis的优点.md)
- 🍁[MyBatis使用介绍](MyBatis/2-MyBatis使用介绍.md)
- 🍁[逆向工程](MyBatis/3-逆向工程.md)
- 🍁[Mybatis源码相关类](MyBatis/4-MyBatis源码.md)
- 🍁[Mybatis拦截器](MyBatis/5-Mybatis拦截器.md)

<a id="topic-redis"></a>
## **Redis**

- 🍅[Redis内存淘汰策略](Redis/1-Redis内存淘汰策略.md)
- 🍅[Redis数据类型以及数据结构实现](Redis/2-Redis数据类型以及数据结构实现.md)
- 🍅[Redis缓存](Redis/3-Redis缓存.md)
- 🍅[Redis哨兵-复制](Redis/4-Redis哨兵-复制.md)
- 🍅[Redis-Cluster集群](Redis/5-Redis-Cluster集群.md)
- 🍅[Redis分布式锁](Redis/6-Redis分布式锁.md)
- 🍅[Redis持久化](Redis/7-Redis持久化.md)
- 🍅[Redis应用场景分析](Redis/8-Redis应用场景分析.md)
- 🍅[Redis-conf配置文件解析](Redis/9-Redis-conf配置文件解析.md)
- 🍅[Redis运维系统命令](Redis/999-Redis运维系统命令.md)

## **Kafka**


- [1-Kafka基础](Kafka/1-Kafka基础.md)

    

<a id="topic-rocketmq"></a>
## **RocketMQ**

- 👲[Windows安装RocketMQ以及运行第一个MQ程序](RocketMQ/0-windows安装RocketMQ以及运行第一个MQ程序.md)
- 👲[RocketMQ核心知识](RocketMQ/1-RocketMQ核心知识.md)
- 👲[RocketMQ最佳实践-来自官网](RocketMQ/2-RocketMQ最佳实践-来自官网.md)
- 👲[RocketMQ控制台搭建](RocketMQ/3-RocketMQ控制台搭建.md)
- 👲[RocketMQ源码片段阅读(一)](RocketMQ/4-RocketMQ源码片段阅读%28一%29.md)

- [RocketMQ配置技能](RocketMQ/999-RocketMQ配置技能.md)
- [RocketMQ配置参数大全-持续收录](RocketMQ/9999-RocketMQ配置参数大全-持续收录.md)
- 👲参与开源
    - 1.[翻译客户端配置部分-中文翻译成英文](RocketMQ/参与阿里巴巴RocketMQ项目/1-客户端配置_en.md)

<a id="topic-netty"></a>
## **NIO/Netty**

- 1.[Netty第一个程序示例](src/main/java/org/byron4j/cookbook/netty)
- 2.[Netty自定义传输协议](src/main/java/org/byron4j/cookbook/netty)
- 3.[Netty登陆请求、验证登陆、获取响应示例](src/main/java/org/byron4j/cookbook/netty)

## **Tomcat**

## **HttpClient4**

- 🎽基础
  
- 🎽高级

<a id="topic-jvm"></a>
## **JVM**

- 💥[JVM参数](JVM/1-JVM参数.md)
- 💥[jstat命令详解](JVM/2-jstat命令详解.md)
- 💥[JVM内存分配与回收策略案例](JVM/11-内存分配与回收策略案例.md)

<a id="topic-zookeeper"></a>
## **Zookeeper**

- 🏃1.[ZK概览](Zokeeper/Zookeeper分布式过程协同技术详解Note/第1章-Zk概览.md)
- 🏃2.[了解Zk与Zk集群配置、主从模式案例演示](Zokeeper/Zookeeper分布式过程协同技术详解Note/第2章-了解Zk与Zk集群配置、主从模式案例演示.md)
- 🏃3.[使用Zk的API进行开发](Zokeeper/Zookeeper分布式过程协同技术详解Note/第3章-使用Zk的API进行开发.md)
- 🏃4.[监听节点的状态变更、分配任务](Zokeeper/Zookeeper分布式过程协同技术详解Note/第4章-处理状态变化-主节点、从节点.md)
- 🏃5.[ZooKeeper内部原理--群首选举等](Zokeeper/Zookeeper分布式过程协同技术详解Note/第九章-ZooKeeper内部原理.md)

## **Dubbo**

- 😇[]()

- RPC
    - Protobuf
        - [Protobuf基础教程](Protobuf/ProtobufTutorial/Protobuf基础教程.md)

## **Atomikos分布式事务方案**

- [官网](https://www.atomikos.com/)

## Code Refactoring（代码重构）

## **Linux**

- 🐧[1-Linux基础](Linux/1-Linux基础.md)

- 🐧[2-Linux用户磁盘管理](Linux/2-Linux用户磁盘管理.md)

- 🐧[3-Linux的vi、vim使用](Linux/3-Linux的vi、vim使用.md)

- 🐧[4-Linux的yum命令](Linux/4-Linux的yum命令.md)

- 🐧[5-yum源配置](Linux/5-yum源配置.md)

- 🐧[6-常用操作快捷键](Linux/6-常用操作快捷键.md)

- 🐧[7- Linux环境下的各种常用开发软件安装教程](Linux/7- Linux环境下的各种常用开发软件安装教程.md)

    ### 🐧awk 脚本语言

    - 🐧[AWK 脚本语言](Linux/awk/1-awk入门.md)

    ### 🐧Shell 脚本编程语言

    - 🐧[shell基础入门知识](Linux/shell/1-shell基础教程.md)
    - 🐧[shell基本语法](Linux/shell/2-shell基本语法.md)
    - 🐧[shell运算符汇总](Linux/shell/3-shell运算符汇总.md)
    - 🐧[shell的echo、printf、test详细介绍](Linux/shell/4-shell的echo、printf、test详细介绍.md)

## **Python3**

- 1.[概览](Python3/1-Python3概览.md)
- 2.[Python3 基本语法、操作运算符](Python3/2-Python3基本语法.md)
- 3.[Python3 分支决策、循环控制](Python3/2-Python3基本语法.md)
- 4.[Python3 迭代器、生成器](Python3/2-Python3基本语法.md)

- Lua 编程语言
    - 1.[Lua 编程语言入门指南](Lua/1-Lua语言入门指南.md)

## **JSON**

### [**Fastjson**](https://github.com/alibaba/fastjson)

### net.sf.json

### [**Jackson**](JSON/jackson/jackson编程指南.md)

### **Gson**

### 应用场景示例

- bean转String
- String转bean
- List转String
- String转List
- Map转String
- String转Map

## **Mockito**

- Mockito基础
    - [首次使用Mockito](Mockito/Mockito基础/1-开始@Mock-@Spy-@Captor-@InjectMocks.md)
- Mockito高级
- Mockito集成

## **分布式配置中心**

### 百度Disconf

- [github地址](https://github.com/knightliao/disconf)
- [文档](https://disconf.readthedocs.io/zh_CN/latest/)

<a id="topic-architecture"></a>
## **架构**

### 🏡SOA面向服务架构

### 🏡微服务

- [微服务+分布式+性能优化+JVM调优+团队开发](https://yq.aliyun.com/articles/661448?spm=a2c4e.11153940.0.0.71d76c14KsnDF1)

- 🏡ESB企业服务总线

- 🏡==[架构资源](架构/内容/架构内容.md)==

   🏡==[缓存](架构/缓存/01_mysql和redis缓存一致性解决方案.png)==

   🏡==[高可用](架构/缓存/01_mysql和redis缓存一致性解决方案.png)==

   🏡==[存储](架构/缓存/01_mysql和redis缓存一致性解决方案.png)==

### 外部资源

- [为什么大公司一定要使用微服务](架构/微服务/01-为什么大公司一定要使用微服务.md)

## **研发管理**

### Scrum

- [Scrum基础入门](Scrum/1-Scrum基础入门.md)    

- 初中级Java工程师技术栈（建设中）

## **术语**        

## **精华资源链接阅读**

- [简易RPC框架-客户端限流配置](http://www.cnblogs.com/ASPNET2008/p/7712974.html)
- [简易RPC框架-SPI](https://www.cnblogs.com/ASPNET2008/p/9062341.html)        
- [简易RPC框架-熔断降级机制](https://www.cnblogs.com/ASPNET2008/p/7954782.html)
- [spring mvc+ELK从头开始搭建日志平台](https://www.cnblogs.com/ASPNET2008/p/5594479.html)
- [简易RPC框架-过滤器机制](http://www.cnblogs.com/ASPNET2008/p/7636276.html)
- [Java-SPI机制解读](https://zhuanlan.zhihu.com/p/28909673)
- [加载时织入可以查看在Spring框架中通过AspectJ织入](https://docs.spring.io/spring/docs/5.1.6.RELEASE/spring-framework-reference/core.html#aop-aj-ltw)
- [Aspectj开发指南](https://www.eclipse.org/aspectj/doc/released/devguide/index.html)
- [Spring中的分布式事务，使用XA和不使用XA](https://www.javaworld.com/javaworld/jw-01-2009/jw-01-spring-transactions.html)
- [Java事务设计策略](https://www.infoq.com/minibooks/JTDS)
- [腾讯云-Mybatis拦截器专栏](https://cloud.tencent.com/developer/information/mybatis%E6%8B%A6%E6%88%AA%E5%99%A8)
- [Mybatis拦截器CSDN](https://blog.csdn.net/zsj777/article/details/81986096)

## **GitHub资源分享**

- [阿里oldratlee](https://github.com/oldratlee)


## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=Byron4j/CookBook&type=Date)](https://star-history.com/#Byron4j/CookBook&Date)

