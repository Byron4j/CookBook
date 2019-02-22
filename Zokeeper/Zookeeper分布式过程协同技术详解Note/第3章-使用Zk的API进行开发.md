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

为了从 Zk 接收通知，需要实现监视点。 

我们先来了解一下 ```org.apache.zookeeper.Watcher``` 接口：

```java
public interface Watcher {
    abstract public void process(WatchedEvent event);
}
```

我们需要实现该接口，定义自己的业务逻辑。

```java
package org.byron4j.cookbook.zk.zkApi;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * 建立自己的监视点（Watcher）
 */

public class Master implements Watcher {

    ZooKeeper zk;

    String hostPort;

    Master(String hostPort){
        this.hostPort = hostPort;
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("process: " + event);
    }

    void startZk() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }

    public static void main(String[] args) throws Exception {
        Master master = new Master("localhost:2181,localhost:2182,localhost:2183");

        master.startZk();

        Thread.sleep(60000);
    }
}


```


- **注意事项**：在构造函数中，并未初始化Zk对象，而是先保存属性hostPort留在了后面使用。JAVA最佳实践告诉我们。**一个对象的构造器还没有完成之前不要调用这个对象的其他方法。** 因为这个对象实现了 Watcher， 且当实例化Zk对象时，其 Watcher 的回调函数就会被调用。所以需要在 Master 的构造函数返回后再调用Zk的构造函数。

- 使用 Master 对象来构造 Zk 对象， 以便添加 Watcher 的回调函数

- 这是一个简单的示例，仅仅对监听到的事件进行了简单的输出

- **连接到Zk后，后台会有一个线程来维护这个Zk会话**。该线程为守护线程，所以即使它处在活跃状态时也可以退出程序。我们让main方法休眠一段时间，以便看到后台线程事件的发生。




运行，输出：

```

11:58:14.913 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:zookeeper.version=3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03, built on 06/29/2018 00:39 GMT
11:58:14.918 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:host.name=DESKTOP-V0N5T5P
11:58:14.918 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:java.version=1.8.0_144
11:58:14.918 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:java.vendor=Oracle Corporation
11:58:14.918 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:java.home=C:\Program Files\Java\jdk1.8.0_144\jre
11:58:14.918 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:java.class.path=C:\Program Files\Java\jdk1.8.0_144\jre\lib\charsets.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\deploy.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\ext\access-bridge-64.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\ext\cldrdata.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\ext\dnsns.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\ext\jaccess.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\ext\jfxrt.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\ext\localedata.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\ext\nashorn.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\ext\sunec.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\ext\sunjce_provider.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\ext\sunmscapi.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\ext\sunpkcs11.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\ext\zipfs.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\javaws.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\jce.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\jfr.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\jfxswt.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\jsse.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\management-agent.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\plugin.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\resources.jar;C:\Program Files\Java\jdk1.8.0_144\jre\lib\rt.jar;F:\217my_optLogs\001系统相关\系统设计\007\CookBook\target\classes;F:\.m2\repository\org\springframework\boot\spring-boot-starter-webflux\2.0.6.RELEASE\spring-boot-starter-webflux-2.0.6.RELEASE.jar;F:\.m2\repository\org\springframework\boot\spring-boot-starter\2.0.6.RELEASE\spring-boot-starter-2.0.6.RELEASE.jar;F:\.m2\repository\org\springframework\boot\spring-boot\2.0.6.RELEASE\spring-boot-2.0.6.RELEASE.jar;F:\.m2\repository\org\springframework\spring-context\5.0.10.RELEASE\spring-context-5.0.10.RELEASE.jar;F:\.m2\repository\org\springframework\spring-aop\5.0.10.RELEASE\spring-aop-5.0.10.RELEASE.jar;F:\.m2\repository\org\springframework\spring-expression\5.0.10.RELEASE\spring-expression-5.0.10.RELEASE.jar;F:\.m2\repository\org\springframework\boot\spring-boot-autoconfigure\2.0.6.RELEASE\spring-boot-autoconfigure-2.0.6.RELEASE.jar;F:\.m2\repository\org\springframework\boot\spring-boot-starter-logging\2.0.6.RELEASE\spring-boot-starter-logging-2.0.6.RELEASE.jar;F:\.m2\repository\ch\qos\logback\logback-classic\1.2.3\logback-classic-1.2.3.jar;F:\.m2\repository\ch\qos\logback\logback-core\1.2.3\logback-core-1.2.3.jar;F:\.m2\repository\org\apache\logging\log4j\log4j-to-slf4j\2.10.0\log4j-to-slf4j-2.10.0.jar;F:\.m2\repository\org\apache\logging\log4j\log4j-api\2.10.0\log4j-api-2.10.0.jar;F:\.m2\repository\org\slf4j\jul-to-slf4j\1.7.25\jul-to-slf4j-1.7.25.jar;F:\.m2\repository\javax\annotation\javax.annotation-api\1.3.2\javax.annotation-api-1.3.2.jar;F:\.m2\repository\org\yaml\snakeyaml\1.19\snakeyaml-1.19.jar;F:\.m2\repository\org\springframework\boot\spring-boot-starter-json\2.0.6.RELEASE\spring-boot-starter-json-2.0.6.RELEASE.jar;F:\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.9.7\jackson-databind-2.9.7.jar;F:\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.9.0\jackson-annotations-2.9.0.jar;F:\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.9.7\jackson-core-2.9.7.jar;F:\.m2\repository\com\fasterxml\jackson\datatype\jackson-datatype-jdk8\2.9.7\jackson-datatype-jdk8-2.9.7.jar;F:\.m2\repository\com\fasterxml\jackson\datatype\jackson-datatype-jsr310\2.9.7\jackson-datatype-jsr310-2.9.7.jar;F:\.m2\repository\com\fasterxml\jackson\module\jackson-module-parameter-names\2.9.7\jackson-module-parameter-names-2.9.7.jar;F:\.m2\repository\org\springframework\boot\spring-boot-starter-reactor-netty\2.0.6.RELEASE\spring-boot-starter-reactor-netty-2.0.6.RELEASE.jar;F:\.m2\repository\io\projectreactor\ipc\reactor-netty\0.7.10.RELEASE\reactor-netty-0.7.10.RELEASE.jar;F:\.m2\repository\io\netty\netty-codec-http\4.1.29.Final\netty-codec-http-4.1.29.Final.jar;F:\.m2\repository\io\netty\netty-codec\4.1.29.Final\netty-codec-4.1.29.Final.jar;F:\.m2\repository\io\netty\netty-handler\4.1.29.Final\netty-handler-4.1.29.Final.jar;F:\.m2\repository\io\netty\netty-buffer\4.1.29.Final\netty-buffer-4.1.29.Final.jar;F:\.m2\repository\io\netty\netty-transport\4.1.29.Final\netty-transport-4.1.29.Final.jar;F:\.m2\repository\io\netty\netty-resolver\4.1.29.Final\netty-resolver-4.1.29.Final.jar;F:\.m2\repository\io\netty\netty-handler-proxy\4.1.29.Final\netty-handler-proxy-4.1.29.Final.jar;F:\.m2\repository\io\netty\netty-codec-socks\4.1.29.Final\netty-codec-socks-4.1.29.Final.jar;F:\.m2\repository\io\netty\netty-transport-native-epoll\4.1.29.Final\netty-transport-native-epoll-4.1.29.Final-linux-x86_64.jar;F:\.m2\repository\io\netty\netty-common\4.1.29.Final\netty-common-4.1.29.Final.jar;F:\.m2\repository\io\netty\netty-transport-native-unix-common\4.1.29.Final\netty-transport-native-unix-common-4.1.29.Final.jar;F:\.m2\repository\org\hibernate\validator\hibernate-validator\6.0.13.Final\hibernate-validator-6.0.13.Final.jar;F:\.m2\repository\javax\validation\validation-api\2.0.1.Final\validation-api-2.0.1.Final.jar;F:\.m2\repository\org\jboss\logging\jboss-logging\3.3.2.Final\jboss-logging-3.3.2.Final.jar;F:\.m2\repository\com\fasterxml\classmate\1.3.4\classmate-1.3.4.jar;F:\.m2\repository\org\springframework\spring-web\5.0.10.RELEASE\spring-web-5.0.10.RELEASE.jar;F:\.m2\repository\org\springframework\spring-beans\5.0.10.RELEASE\spring-beans-5.0.10.RELEASE.jar;F:\.m2\repository\org\springframework\spring-webflux\5.0.10.RELEASE\spring-webflux-5.0.10.RELEASE.jar;F:\.m2\repository\org\synchronoss\cloud\nio-multipart-parser\1.1.0\nio-multipart-parser-1.1.0.jar;F:\.m2\repository\org\synchronoss\cloud\nio-stream-storage\1.1.3\nio-stream-storage-1.1.3.jar;F:\.m2\repository\org\projectlombok\lombok\1.16.22\lombok-1.16.22.jar;F:\.m2\repository\org\hamcrest\hamcrest-core\1.3\hamcrest-core-1.3.jar;F:\.m2\repository\org\springframework\spring-core\5.0.10.RELEASE\spring-core-5.0.10.RELEASE.jar;F:\.m2\repository\org\springframework\spring-jcl\5.0.10.RELEASE\spring-jcl-5.0.10.RELEASE.jar;F:\.m2\repository\io\projectreactor\reactor-core\3.1.10.RELEASE\reactor-core-3.1.10.RELEASE.jar;F:\.m2\repository\org\reactivestreams\reactive-streams\1.0.2\reactive-streams-1.0.2.jar;F:\.m2\repository\junit\junit\4.12\junit-4.12.jar;F:\.m2\repository\mysql\mysql-connector-java\5.1.47\mysql-connector-java-5.1.47.jar;F:\.m2\repository\com\zaxxer\HikariCP\2.5.1\HikariCP-2.5.1.jar;F:\.m2\repository\org\slf4j\slf4j-api\1.7.25\slf4j-api-1.7.25.jar;F:\.m2\repository\org\apache\commons\commons-pool2\2.5.0\commons-pool2-2.5.0.jar;F:\.m2\repository\net\sf\json-lib\json-lib\2.4\json-lib-2.4-jdk15.jar;F:\.m2\repository\commons-beanutils\commons-beanutils\1.8.0\commons-beanutils-1.8.0.jar;F:\.m2\repository\commons-collections\commons-collections\3.2.1\commons-collections-3.2.1.jar;F:\.m2\repository\commons-lang\commons-lang\2.5\commons-lang-2.5.jar;F:\.m2\repository\commons-logging\commons-logging\1.1.1\commons-logging-1.1.1.jar;F:\.m2\repository\net\sf\ezmorph\ezmorph\1.0.6\ezmorph-1.0.6.jar;F:\.m2\repository\com\alibaba\fastjson\1.2.55\fastjson-1.2.55.jar;F:\.m2\repository\org\apache\zookeeper\zookeeper\3.4.13\zookeeper-3.4.13.jar;F:\.m2\repository\org\slf4j\slf4j-log4j12\1.7.25\slf4j-log4j12-1.7.25.jar;F:\.m2\repository\log4j\log4j\1.2.17\log4j-1.2.17.jar;F:\.m2\repository\jline\jline\0.9.94\jline-0.9.94.jar;F:\.m2\repository\org\apache\yetus\audience-annotations\0.5.0\audience-annotations-0.5.0.jar;F:\.m2\repository\io\netty\netty\3.10.6.Final\netty-3.10.6.Final.jar;C:\Program Files\JetBrains\IntelliJ IDEA 2018.2.4\lib\idea_rt.jar
11:58:14.923 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:java.library.path=C:\Program Files\Java\jdk1.8.0_144\bin;C:\WINDOWS\Sun\Java\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\Program Files (x86)\Common Files\Oracle\Java\javapath;C:\ProgramData\Oracle\Java\javapath;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\Program Files\Java\jdk1.8.0_144\bin;F:\apache-maven-3.2.5\bin;C:\Program Files\Git\cmd;C:\Program Files\TortoiseGit\bin;C:\Program Files\MySQL\MySQL Server 5.7\bin;C:\Program Files\TortoiseSVN\bin;C:\WINDOWS\System32\OpenSSH\;%CURL_HOME%;%CONSULE_HOME%;E:\111softwares\consul_1.0.7_windows_386;C:\Program Files (x86)\sbt\bin;C:\Program Files (x86)\scala\bin;C:\Program Files\nodejs\;C:\Program Files (x86)\PuTTY\;C:\Program Files\PuTTY\;F:\instroll\python3\Scripts\;F:\instroll\python3\;C:\Users\BYRON.Y.Y\AppData\Local\Microsoft\WindowsApps;;C:\Program Files\Microsoft VS Code\bin;F:\instroll\GoLand 2018.3\bin;;C:\Users\BYRON.Y.Y\AppData\Roaming\npm;.
11:58:14.923 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:java.io.tmpdir=C:\Users\BYRONY~1.Y\AppData\Local\Temp\
11:58:14.923 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:java.compiler=<NA>
11:58:14.923 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:os.name=Windows 10
11:58:14.923 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:os.arch=amd64
11:58:14.923 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:os.version=10.0
11:58:14.923 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:user.name=BYRON.Y.Y
11:58:14.923 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:user.home=C:\Users\BYRON.Y.Y
11:58:14.923 [main] INFO org.apache.zookeeper.ZooKeeper - Client environment:user.dir=F:\217my_optLogs\001系统相关\系统设计\007\CookBook
11:58:14.925 [main] INFO org.apache.zookeeper.ZooKeeper - Initiating client connection, connectString=localhost:2181,localhost:2182,localhost:2183 sessionTimeout=15000 watcher=Master@c2e1f26
11:58:14.930 [main] DEBUG org.apache.zookeeper.ClientCnxn - zookeeper.disableAutoWatchReset is false
11:58:15.660 [main-SendThread(localhost:2183)] INFO org.apache.zookeeper.ClientCnxn - Opening socket connection to server localhost/0:0:0:0:0:0:0:1:2183. Will not attempt to authenticate using SASL (unknown error)
11:58:15.663 [main-SendThread(localhost:2183)] INFO org.apache.zookeeper.ClientCnxn - Socket connection established to localhost/0:0:0:0:0:0:0:1:2183, initiating session
11:58:15.670 [main-SendThread(localhost:2183)] DEBUG org.apache.zookeeper.ClientCnxn - Session establishment request sent on localhost/0:0:0:0:0:0:0:1:2183
11:58:15.826 [main-SendThread(localhost:2183)] INFO org.apache.zookeeper.ClientCnxn - Session establishment complete on server localhost/0:0:0:0:0:0:0:1:2183, sessionid = 0x368ff574d120001, negotiated timeout = 15000
process: WatchedEvent state:SyncConnected type:None path:null
11:58:20.683 [main-SendThread(localhost:2183)] DEBUG org.apache.zookeeper.ClientCnxn - Got ping response for sessionid: 0x368ff574d120001 after 7ms
11:58:25.684 [main-SendThread(localhost:2183)] DEBUG org.apache.zookeeper.ClientCnxn - Got ping response for sessionid: 0x368ff574d120001 after 0ms
11:58:30.684 [main-SendThread(localhost:2183)] DEBUG org.apache.zookeeper.ClientCnxn - Got ping response for sessionid: 0x368ff574d120001 after 0ms
11:58:35.685 [main-SendThread(localhost:2183)] DEBUG org.apache.zookeeper.ClientCnxn - Got ping response for sessionid: 0x368ff574d120001 after 0ms
11:58:40.686 [main-SendThread(localhost:2183)] DEBUG org.apache.zookeeper.ClientCnxn - Got ping response for sessionid: 0x368ff574d120001 after 1ms
11:58:45.686 [main-SendThread(localhost:2183)] DEBUG org.apache.zookeeper.ClientCnxn - Got ping response for sessionid: 0x368ff574d120001 after 1ms
11:58:50.685 [main-SendThread(localhost:2183)] DEBUG org.apache.zookeeper.ClientCnxn - Got ping response for sessionid: 0x368ff574d120001 after 0ms
11:58:55.686 [main-SendThread(localhost:2183)] DEBUG org.apache.zookeeper.ClientCnxn - Got ping response for sessionid: 0x368ff574d120001 after 2ms
11:59:00.686 [main-SendThread(localhost:2183)] DEBUG org.apache.zookeeper.ClientCnxn - Got ping response for sessionid: 0x368ff574d120001 after 1ms
11:59:05.686 [main-SendThread(localhost:2183)] DEBUG org.apache.zookeeper.ClientCnxn - Got ping response for sessionid: 0x368ff574d120001 after 0ms
11:59:10.687 [main-SendThread(localhost:2183)] DEBUG org.apache.zookeeper.ClientCnxn - Got ping response for sessionid: 0x368ff574d120001 after 1ms

Process finished with exit code 0

```

我们来看看程序运行日志的输出信息：

- 前面几行描述了Zk客户端的实现和环境
- 接着输出了连接建立后，该连接的相关信息，包括客户端所连接的 主机、端口信息，sessionId、以及会话超时时间等
- 还看到程序输出了 WatcherEvent 对象

### 场景化运行 Watcher 示例

前面运行的是服务器正常时的输出。

如果我们不启动服务器，直接运行服务呢？提示连接不上的错误，并且Watcher对象也没有输出，因为根本连接不上Zk：

```
12:37:34.909 [main-SendThread(localhost:2181)] INFO org.apache.zookeeper.ClientCnxn - Socket error occurred: localhost/0:0:0:0:0:0:0:1:2181: Connection refused: no further information
12:37:34.915 [main-SendThread(localhost:2181)] DEBUG org.apache.zookeeper.ClientCnxnSocketNIO - Ignoring exception during shutdown input
java.nio.channels.ClosedChannelException: null
	at sun.nio.ch.SocketChannelImpl.shutdownInput(SocketChannelImpl.java:780)
	at sun.nio.ch.SocketAdaptor.shutdownInput(SocketAdaptor.java:399)
	at org.apache.zookeeper.ClientCnxnSocketNIO.cleanup(ClientCnxnSocketNIO.java:200)
	at org.apache.zookeeper.ClientCnxn$SendThread.cleanup(ClientCnxn.java:1250)
	at org.apache.zookeeper.ClientCnxn$SendThread.run(ClientCnxn.java:1174)
```


重新正常启动服务完毕，然后运行 Master 程序，然后停止服务器并保持 Master 继续运行，再重启服务器，会看到什么呢？
我们看到了在 **SyncConnected** 事件之后发生了 **Disconnected**  事件。
重启服务器后，又是一个 **SyncConnected** 事件。

```
12:43:00.917 [main-SendThread(localhost:2181)] INFO org.apache.zookeeper.ClientCnxn - Session establishment complete on server localhost/0:0:0:0:0:0:0:1:2181, sessionid = 0x169040f04740000, negotiated timeout = 15000
process: WatchedEvent state:SyncConnected type:None path:null
12:43:05.037 [main-SendThread(localhost:2181)] INFO org.apache.zookeeper.ClientCnxn - Unable to read additional data from server sessionid 0x169040f04740000, likely server has closed socket, closing socket connection and attempting reconnect
process: WatchedEvent state:Disconnected type:None path:null
12:43:05.287 [main-SendThread(localhost:2183)] INFO org.apache.zookeeper.ClientCnxn - Opening socket connection to server localhost/127.0.0.1:2183. Will not attempt to authenticate using SASL (unknown error)

...

12:43:29.330 [main-SendThread(localhost:2181)] INFO org.apache.zookeeper.ClientCnxn - Session establishment complete on server localhost/127.0.0.1:2181, sessionid = 0x169040f04740000, negotiated timeout = 15000
process: WatchedEvent state:SyncConnected type:None path:null
12:43:34.374 [main-SendThread(localhost:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Got ping response for sessionid: 0x169040f04740000 after 54ms
```

>**注意：**
>
>当开发者看到 Disconnected 事件的时候，有些人可能认为需要创建一个新的Zk句柄来重连服务。
>
>实际上并不需要！我们看到**SyncConnected** 事件之后发生了 **Disconnected**  事件。重启服务器后，又是一个 **SyncConnected** 事件。
>
>Zk客户端负责为你重新连接服务，当遇到网络故障时，Zk可以处理这些故障问题。


>**Zookeeepr管理连接：**
>
>请不要自己尝试去管理Zk客户端连接。Zk客户端会监控与服务之间的连接，客户端不仅告诉我们连接发生的问题，还会尝试主动建立通信。
>
>一般而言，客户端会很快重建会话，以便最小化应用的影响。所以不要关闭会话后在启动一个新的会话，这样反而增加了系统负载，并导致更长时间的中断。


Zk有两种管理接口：JMX和四个字母组成的命令。现在我们通过 **stat**、**dump** 来看看服务端发生了什么。

 
 >**关闭会话：**
 >
 >当程序功能执行完毕后，最好的方式是关闭会话。可以通过 ZooKeeper.close() 方法来结束，一旦调用close方法后，Zk对象实例所表示的会话就会被销毁。
 
 
 让我们在示例程序 Master 中加入 close 调用：
 
 ```java
    void stopZk() throws InterruptedException {
         zk.close();
     }
     
    public static void main(String[] args) throws Exception {
        Master master = new Master("localhost:2181,localhost:2182,localhost:2183");

        master.startZk();

        Thread.sleep(60000);

        master.stopZk();
    }     
 ```
 
 ## 获取管理权
 
 多个进程创建 /master 节点，但是只有一个会成功，称为主节点。
 
 ZK 的常量 **ZooDefs.Ids.OPEN_ACL_UNSAFE** 为所有进程提供了所有权限（但是正如其名，这个ACL策略在某些不可信环境下是不安全的）。
 
 Zk 通过插件式的认证方法提供了每个节点的ACL策略功能，因此可以限制某个用户对某个znode的哪些权限。
 我们希望在主节点死后 /master 节点消失，我们需要创建临时性节点 **EPHEMERAL**。
 我们在程序中增加以下代码：
 
 ```java
    String serverId = Integer.toHexString(new Random().nextInt());
...

    void runForMaster() throws KeeperException, InterruptedException {
        /*
            path， 节点数据， 访问控制策略， 节点模式
         */
        zk.create("/master", serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }
    
...

    public static void main(String[] args) throws Exception {
        Master master = new Master("localhost:2181,localhost:2182,localhost:2183");

        master.startZk();

        master.runForMaster();

        Thread.sleep(60000);

        master.stopZk();
    }
 ```
 
 运行程序输出了一个新的创建 /master 节点的日志信息：
 
 ```
 16:51:43.286 [main-SendThread(localhost:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Reading reply sessionid:0x1690410d9190001, packet:: clientPath:null serverPath:null finished:false header:: 1,1  replyHeader:: 1,21474836488,0  request:: '/master,#6533383765613230,v{s{31,s{'world,'anyone}}},1  response:: '/master 

 ```

 - 尝试创建znode节点： /master。 如果这个znode存在，create就会失败。同时在 /master 节点的数据字段保存了一个随机的serverId。
 
 - 节点数据字段只能存放字节数组类型
 
 - 我们使用开放的 ACL 策略
 
 - 创建的节点类型为临时性的： **EPHEMERAL**
 
 我们看到 create 方法需要捕获2个异常： ```KeeperException```, ```InterruptedException```。
 我们需要确保处理了这两种异常，ConnectionLossException（KeeperException 的子类）和 InterruptedException，因为对于这两种异常，create方法有可能已经成功了。
 
 **ConnectionLossException** 异常发生于客户端与Zk服务端失去连接时。一般是由于网络原因导致，如网络分区、Zk服务端故障等。当这个异常发生的时候，客户端并不知道在Zk服务器处理前丢失了还是处理完成后丢失的。
 如前面所说，Zk客户端会为丢失的会话重新建立连接，但是进程必须知道一个悬而未决的请求是否已经处理了 or 需要再次发送请求尝试。
 
 **InterruptedException** 异常源于客户端线程调用了 Thread.interrupt ，通常这是因为应用程序部分关闭。进程会终端本地客户端的请求，并使该请求处于未知状态。
 
 当我们在处理这些异常时，必须知道系统的状态：
 **如果发生群选举，在没有确认情况之前，不希望确定主节点。**
 **如果create成功了，活动主节点死掉以前，没有任何进程能够成为主节点。**
 **如果活动主节点还不知道自己已经获得了管理权，不会有任何进程成为主节点。**
 
 当处理 ConnectionLossException 异常时，我们需要找出哪个进程创建的 /master 节点，如果进程是自身，就开始称为群首角色。通过 ```getData``` 方法来处理：
 
 ```java
 public byte[] getData(String path, boolean watch, Stat stat)
 ```
 
 其中：
 
- **path**
 
  类似其它Zk方法一样，第一个参数为我们想要获取数据的znode节点的路径
  
- **watcher**

  表示我们是否想要监听后续的数据变更。如果设置为 true， 就可通过创建句柄时所设置的Watcher对象得到事件。
  同时另一个版本的方法提供了以 Watcher 对象作为入参，通过这个传入的对象来接收变更的事件。
  现在我们先设置为false，仅仅想知道当前的数据是什么。
  
- **stat**
  
  最后一个Stat数据结构，getData 方法会填充znode节点的元数据信息。
  
修改一下程序见 Master2，在 funForMaster 方法中引入异常处理的逻辑：

```java
    boolean isLeader = false;

...

    /**
     * 如果存在了 /master 返回true
     * @return
     */
    boolean checkMaster(){
        while(true){
            Stat stat = new Stat();
            try {
                byte[] data = zk.getData("/master", false, stat);

                isLeader = new String(data).equals(serverId);
                return true;
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
...

    void runForMaster() throws InterruptedException {
        while(true){
            try {
                zk.create("/master", serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                // 创建完成，为主节点
                isLeader = true;
                break;
            } catch (KeeperException.NodeExistsException e) {
                isLeader = false;
                break;
            }catch (KeeperException.ConnectionLossException ex){

            }catch (KeeperException ex2){

            }

            // 检查 /master 是否存在
            if(checkMaster()){
                break;
            }

        }

    }
```

运行第一次Master，输出：I'm a leader.
接着立马运行第二次，输出：Someone else is the leader.


这个案例知悉了：

- 通过获取 /master 节点的数据来检查活动主节点： ```byte[] data = zk.getData("/master", false, stat);```
- 获取节点数据后，比较是否和当前服务id一致，一致则为群首：```isLeader = new String(data).equals(serverId);```
- 在 zk.create方法包在try块之中，以便捕获 ConnectionLossException 异常。
- create成功，则为主节点
- 处理 ConnectionLossException 异常时，并没有做任何事情，让他继续循环重新create并判断
- 后面检查主节点是否存在，不存在则重试

在Master2中，我们并没有处理 InterruptedException ，而是简单的将其抛给了上层服务调用。InterruptedException 异常的处理依赖于成俗的上下文环境。

看一下main方法：

```java
    public static void main(String[] args) throws Exception {
        Master2 master = new Master2("localhost:2181,localhost:2182,localhost:2183");

        master.startZk();

        master.runForMaster();

        if(master.isLeader){
            System.out.println("I'm a leader.");
        }else{
            System.out.println("Someone else is the leader.");
        }
        Thread.sleep(60000);
        master.stopZk();
    }
```

- 调用 runForMaster 方法， 当前进程或者其他进程称为主节点时返回
- 当开发主节点的逻辑时：```if(master.isLeader)``` ，在这个分支里执行这些逻辑，当然目前仅仅需要输出简单的文本信息即可

### 异步获取管理权

**在 Zk 中，所有同步调用方法都有对应的异步调用方法。** 通过异步调用，可以在单线程中同时进行多个调用，简化实现方式。

现在将前面的管理权的示例修改为异步调用的方式。

**create** 方法的异步调用定义：

```java
public void create(final String path, 
                    byte data[], 
                    List<ACL> acl,
                    CreateMode createMode,  
                    StringCallback cb, 
                    Object ctx)
```

相比同步调用版本的方法锁了2个参数cb、ctx。

- ```StringCallback cb``` : 提供回调方法的对象
- 用户指定上下文信息（回调方法调用是传入的对象实例）
- 回调对象cb通过传入的上下文参数ctx来获取数据，当从服务器接收到create请求的结果的时候，上下文参数ctx就会通过回调对象cb提供给应用程序

回调对象的实现需要实现 StringCallback 接口, 只有一个方法：

```public void processResult(int rc, String path, Object ctx, String name);```

>**异步方法的调用**：
>
>简单化队列对Zk服务器的请求，并在另一个线程中参数请求。
>
>当接收到响应信息，这些请求就会在一个专用的回调线程中被处理，只会有一个单独的线程按照接收顺序处理响应包。

处理结果方法 processResult 的参数含义如下：

- **rc**

  返回调用的结构，返回 OK 或与 KeeperException 异常对应的编码值
 
- **path**

  传给create方法的path参数的值，即节点的path

- **ctx**

  传递给create方法的上下文参数
 
- **name**

  创建的znode节点名称
  
目前，调用成功后，path 和 name 的值一样，但是如果采用 CreateMode.SEQUENTIAL 模式的话，这两个参数值就不会相等。

>**注意：回调函数处理**
>
>因为只有一个线程负责处理所有的回调调用，如果回调函数阻塞，所有后续的回调调用都会被阻塞，所以一般不要在回调函数中集中操作或阻塞操作。
>
>有时候，在回调函数中调用同步方法是允许的，但是建议最好不要这样做，以便后来的回调调用可以被快速处理。

继续改造我们的程序，见 Master3.java，创建了回调方法对象 masterCreateCallback：

```java
    // 创建回调方法对象
    static AsyncCallback.StringCallback masterCreateCallback = new AsyncCallback.StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    checkMaster();
                    return;
                case OK:
                    isLeader = true;
                    break;
                default:
                    isLeader = false;
            }

            System.out.println("I'm " + (isLeader ? "" : "not ") + "the leader");
        }
    };
```

调整 runForMaster 方法，改为异步调用：

```java
    void runForMaster(){
        zk.create("/master", serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, masterCreateCallback, null);
    }
```

- 从 rc 参数中获得了 create 请求的结果，并将其转换为 KeeperException.Code 枚举类型。rc 如果不为0，则对应 KeeperException 异常。
- 如果因连接丢失导致 create 请求失败，会得到 CONNECTIONLOSS 编码结果。当连接丢失时，需要检查系统当前的状态，并判断需要如何恢复，
- 现在成为了群首，此例还是先简单的将 isLeader 赋值为true表示
- 在 runForMaster 方法中，我们将回调方法对象 masterCreateCallback 传递给 create 方法，null 作为上下文对象参数（因为现在不需要向masterCreateCallback.processResult放啊传入任何信息）。

现在需要实现 checkMaster 的异步方法调用，也通过回调方法来实现逻辑，```getData``` 方法的异步调用方式。
首先实现一个回调方法对象 ```masterCheckCallback``` 实现 ```DataCallback```：

```java
    static AsyncCallback.DataCallback masterCheckCallback = new AsyncCallback.DataCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    checkMaster();
                    return;
                case NONODE:
                    runForMaster();
                    return;
            }
        }
    };
```

checkMaster 方法中则仅保留 getData 方法的异步调用方式代码:

```java
    static void checkMaster(){
        zk.getData("/master",false, masterCheckCallback, null);
    }
```

**同步调用、异步调用要处理的逻辑是一样的，只是异步方法中，我们没有使用while循环，而是通过在异步调用中的回调函数中纪念性的错误处理。**


### 设置元数据

我们的主从模式依赖于三个目录： /workers、/tasks、/assign。
**我们可以在系统启动前通过某些系统环境配置进行创建这些目录，或者通过主节点程序每次启动时都创建这个目录。**

```java
    /**
     * 初始化时，创建主从模式的三个目录
     */
    static void bootstrap(){
        createParent("/workers", new byte[0]);
        createParent("/tasks", new byte[0]);
        createParent("/assign", new byte[0]);
    }
```

使用持久性模式创建 /workers、/tasks、/assign 节点：

```java
    /**
     * 创建 /workers、/tasks、/assign
     * @param path
     * @param data
     */
    static void createParent(String path, byte[] data){
        zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, createParentCallback, data);
    }
```

创建 /workers、/tasks、/assign 的回调方法对象：

```java
    // 创建主从模型目录的回调方法对象
    static AsyncCallback.StringCallback createParentCallback = new AsyncCallback.StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)){
                case OK:
                    log.info("Parent " + path + " created");
                    break;
                case CONNECTIONLOSS:
                    createParent(path, (byte[]) ctx);
                    break;
                case NODEEXISTS:
                    log.warn("Parent already registered: " + path);
                    break;
                default:
                    log.error("Something went wrong: ", KeeperException.create(KeeperException.Code.get(rc), path));
            }
        }
    };
```

至此，完整的示例代码见 Master4.java， 运行程序之前，我们先看一下当前的znode节点：

```
[zk: localhost:2181,localhost:2182,localhost:2183(CONNECTED) 6] ls /
[zookeeper, workers, tasks, assign]
[zk: localhost:2181,localhost:2182,localhost:2183(CONNECTED) 7]

```

这是因为我们在前面使用 ZkCli 演示Zk相关命令创建的，现在删除这些节点后(**非空节点使用 ```rmr /path``` 删除**)，再来运行程序：

```
15:44:00.857 [main-EventThread] INFO Master4 - Parent /workers created
15:44:00.857 [main-EventThread] INFO Master4 - Parent /tasks created
15:44:00.857 [main-EventThread] INFO Master4 - Parent /assign created
```


说明：
- 因为没有数据存入这些znode，所以传入空的字节数组
- ```zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, createParentCallback, data);``` 这一行看起来有点怪异，但是我们需要知道第一个 data 参数表示要保存到znode节点的数据；而第二个 data 则可以在回调函数中继续使用。
- 如果回调函数返回的是 ```CONNECTIONLOSS``` 就重新调用create重试。


## 注册从节点

我们已经有了主节点 Master 了， 还需要配置从节点，以便主节点发号施令。根据前面的设计和ZkCli案例的介绍，每个从节点需要在 /workers 下创建一个临时性znode节点：

```java
package org.byron4j.cookbook.zk.zkFollow;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Random;

@Slf4j
public class Worker implements Watcher {

    ZooKeeper zk;
    String hostPort;
    String serverId = Integer.toHexString(new Random().nextInt());

    @Override
    public void process(WatchedEvent event) {
        log.info("process:" + event + ": " + hostPort);
    }

    /**
     * 构造器
     * @param hostPort
     */
    Worker(String hostPort){
        this.hostPort = hostPort;
    }

    /**
     * 连接Zk服务器
     * @throws IOException
     */
    void startZk() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }

    /**
     * 关闭Zk会话
     * @throws InterruptedException
     */
    void stopZk() throws InterruptedException {
        zk.close();
    }

    // 创建 回调方法对象
    AsyncCallback.StringCallback createWorkCallback = new AsyncCallback.StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    register(); // 连接丢失，则重新连接重试
                    break;
                case OK:
                    log.info("Registered successfully: " + serverId); // 输出创建OK的信息
                    break;
                case NODEEXISTS:
                    log.warn("Already registered: " + serverId); // 已经存在了
                    break;
                default:
                    log.error("Something went wrong:" + KeeperException.create(KeeperException.Code.get(rc), path));
            }
        }
    };

    /**
     * 注册从节点
     */
    void register(){
        zk.create("/workers/worker-" + serverId,
                "Idle".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL,
                createWorkCallback,
                null);
    }



    public static void main(String[] args) throws Exception {
        Worker worker = new Worker("localhost:2181,localhost:2182,localhost:2183");
        worker.startZk(); // 连接Zk

        worker.register(); // 注册从节点

        Thread.sleep(30000);

        worker.stopZk(); // 关闭会话
    }
}

```

- 我们将从节点的状态信息存入代表从节点的znode节点中
- 如果进程死掉，希望代表从节点的znode节点得到清理，所以设置为临时性节点 EPHEMERAL， 这意味着，简单的关注 /workers 节点就可以得到有效从节点的列表

运行程序后可以看到创建了一个从节点：

```
[zk: localhost:2181,localhost:2182,localhost:2183(CONNECTED) 31] ls /workers
[worker-306b249]

```

- 因为这个进程是唯一创建表示该进程的临时性znode节点的进程，如果创建节点时连接丢失，进程就会简单地重试创建过程

我们将从节点信息存入了代表从节点的znode节点，如此就可以通过查询Zk来获取从节点地状态。当前，只有初始化和空闲状态，但是，一旦从节点开始处理某些事情，还需要设置其他状态信息。

**以下是setStat方法的实现，该方法与常规方法有所不同，我们希望异步方式来设置状态，以便不会延迟常规流程的操作：**

```java
    // 异步设置状态
    AsyncCallback.StatCallback statusUpdateCallback = new AsyncCallback.StatCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    updateStatus((String)ctx);
                    return;
            }
        }
    };

    /**
     * 设置节点状态
     * @param status
     */
    synchronized void updateStatus(String status){
        if( this.status == status ){
            zk.setData("/workers/worker-" + serverId, status.getBytes(), -1, statusUpdateCallback, status);
        }
    }

    /**
     * 设置节点状态
     * @param status
     */
    void setStatus(String status){
        this.status = status;
        updateStatus(status);
    }
```

- 我们将状态信息保存到本地变量中ctx="done"，万一更新失败，需要重试
- 我们并未在 setStatus 方法中进行更新，而是新建了一个 updateStatus 方法， 在 setStatus 中使用它，并且可以在重试逻辑中使用
- ```zk.setData("/workers/worker-" + serverId, status.getBytes(), -1, statusUpdateCallback, status);``` 第三个参数 -1 表示禁止版本号检查，通过上下文对象参数即最后一个参数传递状态
- 收到连接丢失的事件则重新更新，updateStatus 方法是一个 synchronized 方法

>**注意：顺序和 ConnectionLossException 异常**
>
>Zk 会严格维护执行顺序，并提供强有力的有序保障，然而，在多线程环境中，需要小心顺序问题。
>
>多线程下，当回调函数中包括重试逻辑的代码时，一些常见的错误都可能发生。
>
>当遇到 ConnectionLossException 异常而补发一个请求时，新建立的请求可能排序在其他线程的请求之后，但是实际上其他线程的请求应该在原来的请求之后。


## 任务队列化

除去 Master、Worker，最后的组件 Client 程序会队列化新任务，以便从节点执行这些任务，我们在 /tasks 节点下面添加子节点来表示需要从节点要执行的命令。

我们使用有序节点来设计，这样的初衷是：
- 序列号指定了任务被队列化的顺序
- 可以通过很少的工作为任务创建唯一的序列号

```java
package org.byron4j.cookbook.zk.zkFollow;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;

@Slf4j
public class Client implements Watcher {

    ZooKeeper zk;
    String hostPort;

    Client(String hostPort){
        this.hostPort = hostPort;
    }

    void startZk() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }

    String queueCommand(String command) throws Exception {
        while(true){
            try{
                String znodeName = zk.create("/tasks/task-", command.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
                return znodeName;
            }catch (KeeperException.NodeExistsException e){
                throw  new Exception("Already exists");
            }catch (KeeperException.ConnectionLossException ex){
                // 连接丢失，则不做任何处理，继续while循环重试
            }

        }
    }

    @Override
    public void process(WatchedEvent event) {

    }

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost:2181,localhost:2182,localhost:2183");
        client.startZk();// 连接到Zk

        String znodeName = client.queueCommand("repayNotify");
        log.info("Created " + znodeName);
    }
}

```

多次运行程序可以看到输出：

>Created /tasks/task-0000000001
>
>......
>
>Created /tasks/task-0000000004



- 在 /tasks 节点下创建 znode 节点来标志一个任务，节点前缀为 task- 。
- 因为采用的是 ```CreateMode.PERSISTENT_SEQUENTIAL``` 模式的节点，task- 后面会接一个单调递增的数字，这样保证每个节点是唯一的，通知 Zk 会确定任务的顺序。因为是持久性节点，所以即使 Client 结束了，该节点依然会存在。
- create 会返回创建的节点的名称，我们可以看到序列号。
- 如果在创建节点时遇到连接丢失，则需要重试，对于绝大都数至少执行一次的任务重试当然是OK的。 但是对于那些最多执行一次的任务，需要为每一个任务制定一个唯一的ID（如会话ID），将其编码到znode节点名中，在遇到连接丢失时，只有在 /tasks 下不存在以这个会话ID命名的节点时才重试命令。



## 管理客户端

最后通过一个程序来 AdminClient 展示系统运行状态。
ZooKeeper 优点之一是可以通过 *ZkCli* 工具来查看系统的状态，但是通常情况下我们需要自己编写管理客户端，以便更快更简单管理系统。
我们通过 ```getData```、```getChildren``` 方法来获得系统状态。

```java
package org.byron4j.cookbook.zk.zkFollow;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Date;

public class AdminClient implements Watcher {

    ZooKeeper zk;
    String hostPort;

    AdminClient(String hostPort){
        this.hostPort = hostPort;
    }

    void startZk() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }


    void listState() throws KeeperException, InterruptedException {
        try {
            Stat stat = new Stat();
            byte[] masterData = zk.getData("/master", false, stat);

            Date startDate = new Date(stat.getCtime());

            System.out.println("Master: " + new String(masterData) + " since " + startDate);
        }catch (KeeperException.NoNodeException e){
            System.out.println("No znode which named Master");
        }

        System.out.println("Workers:");

        for (String w: zk.getChildren("/workers", false)) {
            byte[] data = zk.getData("/workers/" + w, false, null);
            String state = new String(data);
            System.out.println("\t" + w + ": " + state);
        }

        System.out.println("Tasks:");
        for( String t : zk.getChildren("/tasks", null)){
            System.out.println("\t" + t);
        }

    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        AdminClient adminClient = new AdminClient("localhost:2181,localhost:2182,localhost:2183");
        adminClient.startZk();

        adminClient.listState();
    }
}

```

- 我们通过 **State** 结构，可以获得主节点 /master 称为主节点的事件信息。ctime 为该 znode 节点建立的毫秒数。
- **getData** 获取节点数据
- **getChildren** 获取节点的子节点名称

此例我们使用的是简单的同步获取系统状态信息， 可以改写为异步的，此处不再展示。

至此为止， Master、Worker、Client 这些基本实现已经带领我们走进了主从系统的开端，但是目前还没有实际调度起来。

如：
- 当一个任务加入队列，主节点需要唤醒并分配任务给一个从节点
- 从节点需要找出分配给自己的任务
- 任务完成时，客户端需要知道
- 如果主节点故障，另一个等待中的主节点需要接管主节点工作
- 如果从节点故障，已分配的任务需要分配给其他从节点

后续...
