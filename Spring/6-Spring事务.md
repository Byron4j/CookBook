# Spring事务

关于数据库事务、锁可以先行查看此文：[MySQL数据库读写锁示例详解、事务隔离级别示例详解](MySQL/1-MySQL数据库读写锁示例详解、事务隔离级别示例详解.md)。

Spring事务属于Data Access模块中的内容，该模块包含事务管理支持以及其它数据访问的集成。

## 事务管理

全面的事务支持是使用Spring框架的最重要原因之一。Spring为事务管理提供了一个始终如一的抽象，优点如下：
- 提供不同事务的API但是一致的编程模型，如Java事务API（JTA）、JDBC、Hibernate和Java持久化API（JPA）。
- 支持声明式事务
- 比JTA更简单的编程式事务API
- 与Spring数据访问抽象的优秀集成

### Spring框架事务模型的优点

习惯上，Java EE 开发者有两种事务管理方式：全局事务管理、本地事务管理，两者都有很大的局限性。

#### 全局性事务管理

全局事务允许你操作多个事务资源，典型的是关系型数据库和消息队列。应用服务器通过JTA管理全局性事务，而JTA API是非常笨重的。另外，一个JTA的```UserTransaction```通常需要从JNDI中加载资源，意味着使用JTA必须配置JNDI。全局性事务限制了代码的重用性，因为JTA通常只在应用服务器环境中可用。


#### 本地事务管理

本地事务是特定于资源的，例如与JDBC关联的事务。本地事务更容易使用，但是也有一个重大的缺陷：不能跨多个事务资源工作。例如，使用JDBC连接的事务管理代码不能在一个JTA的全局性事务中使用。因为应用服务器不参与事务管理，它不能帮助确保跨多个资源的正确性。


#### Spring框架一致性编程模型

Spring解决了全局性事务和本地事务的缺陷，它可以让应用开发者在任何环境下使用一致的编程模型API。你在一个地方编写你的代码，它可以在不同环境的不同事务管理策略中工作。Spring框架提供了```声明式事务```和```编程式事务```。大都数用户偏爱声明式事务，因为编码更简单。

通过编程式事务，开发者通过Spring框架事务抽象来进行开发，可以运行在任何底层事务基础设施上。
使用首选的声明式事务模型，开发者仅需要编写一点点与事务管理关联的代码，因此，不需要依赖Spring框架事务的API或其他事务API。

## Spring事务相关的类


- org.springframework.transaction.PlatformTransactionManager
- org.springframework.transaction.TransactionDefinition
- org.springframework.transaction.TransactionStatus
- org.springframework.transaction.support.TransactionSynchronization


![](pictures/Spring-tx.png)

Spring框架事务抽象的关键点是事务策略的概念。一个事务策略通过```org.springframework.transaction.PlatformTransactionManager```接口来定义，像以下所展示的：



```java
/**事务管理器*/
public interface PlatformTransactionManager {
    /**根据事务定义获取事务状态*/
    TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException;
    /**提交事务*/
    void commit(TransactionStatus status) throws TransactionException;
    /**回滚事务*/
    void rollback(TransactionStatus status) throws TransactionException;
}
```

这主要是一个服务提供者接口(**SPI**)，尽管你可以使用编程方式使用它。因为```PlatformTransactionManager```是一个接口，它可以根据需要很容易地被mock或作为存根使用。它没有绑定到查找策略，比如JNDI等。
PlatformTransactionManager 实现的定义与Spring框架IOC容器中其他任何bean是一样的，仅这一点就使得Spring事务是一个有价值的抽象，甚至你在使用JTA的时候。

同样，为了保持和Spring理念一致，PlatformTransactionManager 接口的方法可以抛出 ```TransactionException ```异常。
**getTransaction(..)** 方法返回一个 ```TransactionStatus```对象，依赖于一个 ```TransactionDefinition```参数，返回的TransactionStatus可能代表一个新的事务或者一个已经存在的事务（如果当前调用堆栈中存在事务）。后一种情况的含义是，与Java EE事务上下文一样，事务状态与执行线程相关联。




```TransactionDefinition ``` 接口指定了：

- **传播性Propagation**：通常，在事务范围内执行的所有代码都在该事务中运行。但是，事务方法在已存在事务上下文执行时，你可以指定其行为。例如，代码可以继续在已经存在的事务中运行（通过是这样的），或者已存在的事务会挂起然后创建一个新的事务。Spring提供了和EJM CMT类似的所有事务传播性操作。
    - **PROPAGATION_REQUIRED**：支持当前事务，如果当前没有事务则新建一个事务。这是默认的事务传播行为。
    - **PROPAGATION_SUPPORTS**：支持当前事务，如果不存在事务则以非事务形式执行。
    - **PROPAGATION_MANDATORY**：支持当前事务，如果没有事务则抛出异常，transaction synchronization还是可用的。
    - **PROPAGATION_REQUIRES_NEW**：新建一个事务，如果当前存在事务则还会挂起已经存在的事务。
    - **PROPAGATION_NOT_SUPPORTED**：不支持当前事务，总是以非事务方式执行。
    - **PROPAGATION_NEVER**：不支持事务，存在事务则抛出异常，transaction synchronization不可用。
    - **PROPAGATION_NESTED**：如果当前存在事务则在嵌套事务中执行，有点类似PROPAGATION_REQUIRED。

- **隔离性Isolation**：指定了事务的隔离性。
    - **ISOLATION_READ_UNCOMMITTED**：读未提交。可能出现脏读、不可重复读、幻读。这个隔离级别，一个事务可以读取另一个事务未提交的内容。
    - **ISOLATION_READ_COMMITTED**：读已提交。阻止了脏读，但是不可重复读、幻读可能会发生。此级别仅禁止事务读取包含未提交更改的行。
    - **ISOLATION_REPEATABLE_READ**：可重复度。阻止了脏读、不可重复度，但是幻读可能会发生。这个级别禁止事务读取包含未提交更改的行，还禁止一个事务读取行、第二个事务更改行、第一个事务重新读取行，第二次获得不同的值(“不可重复读取”)。
    - **ISOLATION_SERIALIZABLE**：串行化。解决了脏读、不可重复度和幻读的问题。效率低，一般生产不用。

- **超时Timeout**：此事务在超时并由事务基础设施自动回滚之前运行多长时间。

- **是否只读Read-only**：当你的代码仅仅读取数据不会更改数据时可以设置只读属性。

这些设置反映了标准的事务概念。理解这些概念，是使用Spring框架或其它事务管理解决方案的基本前提。

**TransactionStatus** 接口为事务代码提供了一种简单的方法来控制事务执行和查询事务状态。

```java
public interface TransactionStatus extends SavepointManager {

    boolean isNewTransaction();

    boolean hasSavepoint();

    void setRollbackOnly();

    boolean isRollbackOnly();

    void flush();

    boolean isCompleted();

}
```

无论您在Spring中选择声明式事务管理还是编程式事务管理，定义正确的PlatformTransactionManager实现都是绝对必要的。通常是通过依赖注入来定义此实现。

```PlatformTransactionManager``` 实现通常需要了解他们的环境：JDBC，JTA，Hibernate等等。以下示例展示了定义了一个本地的```PlatformTransactionManager```实现（此例中，使用了简单的JDBC）。
你可以像以下一样创建一个类似的beam，定义一个JDBC DataSource：

```xml
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName" value="${jdbc.driverClassName}" />
    <property name="url" value="${jdbc.url}" />
    <property name="username" value="${jdbc.username}" />
    <property name="password" value="${jdbc.password}" />
</bean>
```

与之关联的 ```PlatformTransactionManager ``` bean定义则可以引用 DataSource的定义，例如：

```xml
<bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>
```

注解方式：
```java
@Bean(name = "myTxManager")
public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
}
```




## Spring事务监听器

### 相关类

- org.springframework.transaction.event.TransactionalEventListener
- org.springframework.transaction.event.TransactionPhase

### 事务监听器示例

