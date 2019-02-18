# 开始使用Zk的API进行编程

前面通过 ZkCli 展示了主-从模式的案例，现在开始使用Zk提供的 API 进行开发，展示 创建会话、实现监视点（watcher）。

## 引入 Zookeeper 的依赖包

这里引入最近的 release 版本的zookeeper依赖jar：

```xml
<!-- https://mvnrepository.com/artifact/org.apache.zookeeper/zookeeper -->
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.13</version>
</dependency>
```


## 建立 Zookeeper 会话

**Zk 的 API 围绕着 Zk 的句柄(handler)而构建的，每个 API 调用都需要传递这个句柄。这个句柄代表与Zk间的一个会话。**

在下图中，与Zk服务器建立的一个会话如果断开，这个会话就会迁移到另一台Zk服务器上。只要会话还存活着，句柄就依然有效，Zk会继续保持这个会话连接，以保证与Zk服务器之间的会话存活。

![](pictures/4-会话在服务器之间转移.png)

如果句柄关闭，Zk客户端会告知Zk服务器关闭这个会话。
如果Zk服务器发现这个会话已经挂掉，就会使这个会话无效。
如果客户端尝试使用重新连接到Zk服务器，使用之前无效的句柄进行连接的话，Zk服务器会告知客户端这个会话已经失效，使用该句柄进行任何操作都会返回错误。

创建Zk句柄的构造函数如下所示：

```java
Zookeeper(String connectString, int sessionTimeout, Watcher watcher)
```

其中：

- **connectString**
  包含主机名、Zk服务器的端口。如： localhost:2181,localhost:2182,localhost:2183

- **sessionTimeout**
  毫秒为单位，表示Zk等待客户端通信的最长时间，之后会声明会话死亡。假如该值为15000亦即15秒，当Zk与客户端15秒内无法进行通信，Zk就会终止这个会话。
  
- **watcher**
  用于接收会话事件的一个对象，需要我们自己创建：实现 Watcher 接口，然后传入Zk的构造器。
  客户端使用 Watcher 接口来监控与Zk之间的会话的健康情况。
  与 Zk 失去连接就会发生事件，同样还用于监控 Zk 数据的变化，最后会话过期，也会通过 Watcher 接口传递事件来通知客户端的应用。
  
### 实现一个 Watcher

