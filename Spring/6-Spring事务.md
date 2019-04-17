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


- ```org.springframework.transaction.PlatformTransactionManager```
- ```org.springframework.transaction.TransactionDefinition```
- ```org.springframework.transaction.TransactionStatus```
- ```org.springframework.transaction.support.TransactionSynchronization```
- ```org.springframework.transaction.support.AbstractPlatformTransactionManager``` 其它框架集成Spring一般会继承该类


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
你可以像以下一样创建一个类似的beam，定义一个

- 1.```JDBC DataSource```配置如下：

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


如果你是在Java EE容器中使用JTA，你可以使用一个容器DataSource，可以通过JNDI获取数据源，再结合Spring框架的```JtaTransactionManager```。
- 2.```JTA和JDNI查找配置如下```：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:jee="http://www.springframework.org/schema/jee"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/jee
        https://www.springframework.org/schema/jee/spring-jee.xsd">

    <jee:jndi-lookup id="dataSource" jndi-name="jdbc/jpetstore"/>

    <bean id="txManager" class="org.springframework.transaction.jta.JtaTransactionManager" />

    <!-- other <bean/> definitions here -->

</beans>
```

```JtaTransactionManager``` 不需要知道DataSource（或其他指定的数据源）因为它使用了容器的全局事务管理基础设施。


你也可以使用Hibernate本地事务，像以下示例展示的一样。在此案例中，你需要定义一个Hibernate的```LocalSessionFactoryBean``` bean，则你的应用可以使用来获取Hibernate的会话```session```实例，而DataSource bean则和本地JDBC示例类似。

>❕ ❕
>
>如果```DataSource```(被任何非JTA事务管理器使用的)是在一个Java EE容器中管理且通过JNDI查找到的，则它应该是非事务的，因为Spring框架(而不是Java EE容器)负责管理事务。

在这个案例中的 ```txManager``` bean是一个```HibernateTransactionManager```类型。和```DataSourceTransactionManager```类似，也需要依赖一个DataSource的引用，```HibernateTransactionManager```需要一个```SessionFactory```的引用。示例如下：

```xml
<bean id="sessionFactory" class="org.springframework.orm.hibernate5.LocalSessionFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <property name="mappingResources">
        <list>
            <value>org/springframework/samples/petclinic/hibernate/petclinic.hbm.xml</value>
        </list>
    </property>
    <property name="hibernateProperties">
        <value>
            hibernate.dialect=${hibernate.dialect}
        </value>
    </property>
</bean>

<bean id="txManager" class="org.springframework.orm.hibernate5.HibernateTransactionManager">
    <property name="sessionFactory" ref="sessionFactory"/>
</bean>
```

如果你是使用Hibernate和Java EE容器管理JTA事务，你应该和之前一样使用```JtaTransactionManager```：
```xml
<bean id="txManager" class="org.springframework.transaction.jta.JtaTransactionManager"/>
```

>❕ ❕
>
>如果你使用的是JTA，你的事务管理器则应该看起来很像，不管使用什么数据访问技术，不管是JDBC、Hibernate JPA还是任何其他受支持的技术。这是因为JTA事务是全局性事务，它可以征募任何事务资源。

在这些案例中，应用的代码是不需要变更的。您可以仅通过更改配置来更改事务的管理方式，即使这种变化意味着从本地事务转移到全局事务，或者反之亦然。


## 事务资源同步

怎样创建不同的事务管理器和它们是怎样关联那些需要同步到事务中的资源的（例如，```DataSourceTransactionManager```之余一个JDBC DataSource，```HibernateTransactionManager```之于一个Hibernate的```SessionFactory，等等）现在应该是比较清晰的了。

这个部分描述应用代码（直接或间接使用持久化API如JDBC、Hibernate，或者JPA）怎样确保这些资源是如何创建、复用和清除的。

也讨论事务同步（transaction synchronization）是如何通过关联的```PlatformTransactionManager```触发的。


### 高级同步方法

首选的方法是使用Spring最高级的基于模板的持久性集成api或者使用基于transaction-aware factory的原生的ORM API ben 或者代理 去管理本地资源工厂。
这种transaction-aware（事务感知）解决方案是在内部处理资源、重用、清除，资源的可选事务同步，异常映射。
因此，用户数据访问代码可以不用关心这些处理而仅仅将关注持久化逻辑的编写。
一般而言，你可以使用原生ORM API或者使用JdbcTemplate处理JDBC数据访问。

### 低级同步方法

像```DataSourceUtils```类（for JDBC）一样，```EntityManagerFactoryUtils```类(for JPA),```SessionFactoryUtils```类(for Hibernate  )，等等就是比较低级的API了。
当你想在应用代码中直接处理原生持久API的资源类型的时候，你可以使用这些类确保实例是由Spring框架管理的、事务同步是可选的、异常映射到合适的持久化API中。

例如，在 JDBC 的案例中，在DataSource中用于替代传统的 JDBC的```getConnection()```的方法，可以使用 ```org.springframework.jdbc.datasource.DataSourceUtils```类：

```java
Connection conn = DataSourceUtils.getConnection(dataSource);
```

如果一个已经存在的事务已经有一个连接connection同步给它了，则会返回该connection。否则，该方法会触发创建一个新的connection，它(可选地)同步到任何现有事务，并可用于该事务的后续重用。
像之前提到过的一样，任何 SQLException 都被包装在Spring框架中的```CannotGetJdbcConnectionException```（这是一个Spring框架的未检查unchecked的DataAccessException类型的层次结构之一）。
这种方法提供的信息比从SQLException获得的信息要多，并且确保了跨数据库甚至跨不同持久性技术的可移植性。
这种方式是没有在Spring事务管理机制下工作的，因此，无论你是否使用Spring事务管理机制都可以使用它。

### ```TransactionAwareDataSourceProxy``` 事务感知数据源代理类

在最底层存在 ```TransactionAwareDataSourceProxy```类。这是一个数据源DataSource的代理类，包装了一个数据源并且将其添加到Spring事务的感知中。在这方面，类似于Java EE服务器提供的传统的JNDI数据源。

你应该几乎从不会使用这个类，除非当前的代码必须通过一个标准的JDBC数据源接口调用实现。在这个场景中，这些代码是有用的，但是它参与了Spring管理的事务。你可以使用高级的抽象编写新的代码。

## 声明式事务管理

>❕ ❕
>
>大部分的Spring框架使用者会选择声明式事务管理。这个选择对应用代码影响更小，因此，它更符合非侵入式轻量级容器理念。

<u>**Spring框架的声明式事务管理是通过Spring面向切面编程（AOP）实现的。**</u>

然而，因为事务相关的代码是随Spring框架发行版本一块发布的，可以以样板方式使用，通常不需要理解AOP的概念就可以使用这些代码了。

Spring框架的声明式事务管理机制类似于EJB CMT，在这种情况下，你可以将事务行为(或缺少事务行为)指定到单个方法级别。如果有必要的话，你可以在一个事务上下文中调用```setRollbackOnly()```方法。这两种类型的事务管理的差异在于：

- 不像EJB CMT是绑定了JTA的。Spring框架的声明式事务管理可以在任何环境中工作，它可以通过调整配置文件就可以轻易地和JTA事务、使用JDBC的本地事务、JPA或者Hibernate一块工作。
- 你可以在任何类中使用Spring框架声明式事务，而不是像EJB一样只能指定某些类。
- Spring框架提供了声明式回滚规则，这是和EJB等同的特性。编程式、声明式的回滚规则都提供了。
- Spring框架可以让你通过AOP自定义事务行为。例如，你可以在事务回滚的时候插入自定义行为。还可以添加任意的advice(通知)，以及事务advice。而如果是EJB CMT的话，你不可能影响容器的事务管理机制，除非使用```setRollbackOnly()```。
- Spring框架不像高端应用服务器那样支持在远程调用之间传播事务上下文。如果你需要这个特性，推荐你使用EJB。但是，在你使用该特性之前需要慎重，因为，正常情况下，是不想在远程调用之间传播事务的。

<u>**回滚规则的概念是非常重要的。**</u> 它们可以让你指定哪些异常应该引发自动回滚。你可以在配置中而不是Java代码中指定这些声明。所以，尽管你可以在```TransactionStatus```对象中调用```setRollbackOnly()```方法去回滚当前的事务，大都数情况下你可以指定一个规则，即可以自定义异常必须导致事务回滚。这种选择的重要优点是业务对象不依赖事务基础设施。例如，它们通常不需要导入Spring事务API或者其它Spring API。

尽管EJB容器默认行为是在事务发生系统异常（通常是运行时异常）时自动回滚，EJB CMT并不会在出现应用异常时自动回滚。但是Spring声明式事务的默认行为是允许自定义异常变更回滚策略的。

### 理解Spring声明式事务实现

仅仅告诉你使用 ```@Transactional```注解标注你的类是不够的，添加```EnabledTransactionManagement```到你的配置中，并希望你理解它是如何工作的。为了提供一个深刻的理解，这个部分解释在发生与事务相关的问题时，Speing声明式事务机制的内部工作原理。

掌握Spring框架声明式事务的最重要的概念是通过AOP代理实现的，事务通知由元数据（XML或者基于注解的）驱动。

<u>**AOP与事务元数据的结合产生了一个AOP代理，它使用一个事务拦截器```TransactionInterceptor```和一个适当的```PlatformTransactionManager```实现来驱动围绕方法调用的事务。**</u>

以下是通过事务代理调用方法的概念试图：

![](pictures/Spring声明式事务代理调用方法的概念试图.png)





## Spring事务监听器

### 相关类

- org.springframework.transaction.event.TransactionalEventListener
- org.springframework.transaction.event.TransactionPhase

### 事务监听器示例

