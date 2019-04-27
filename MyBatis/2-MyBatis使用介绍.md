# MyBatis使用介绍

[官网](http://www.mybatis.org/mybatis-3/zh/index.html)

>MyBatis 是一款优秀的持久层框架，它支持定制化 SQL、存储过程以及高级映射。MyBatis 避免了几乎所有的 JDBC 代码和手动设置参数以及获取结果集。MyBatis 可以使用简单的 XML 或注解来配置和映射原生类型、接口和 Java 的 POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录。

## MyBatis 使用步骤

### 引入maven依赖

```xml
<dependency>
  <groupId>org.mybatis</groupId>
  <artifactId>mybatis</artifactId>
  <version>x.x.x</version>
</dependency>
```

### 从XML中构建SqlSessionFactory

每个基于 MyBatis 的应用都是以一个 ```SqlSessionFactory``` 的实例为核心的。
SqlSessionFactory 的实例可以通过 ```SqlSessionFactoryBuilder``` 获得。
SqlSessionFactoryBuilder 则可以从 XML 配置文件或一个预先定制的 Configuration 的实例构建出 SqlSessionFactory 的实例。
MyBatis 包含一个名叫 ```Resources``` 的工具类，它包含一些实用方法，可使从 classpath 或其他位置加载资源文件更加容易。

