
### ZooKeeper实现选举的类是```org.apache.zookeeper.server.quorum.QuorumPeer```

>查找群首leader
>
>```QuorumPeer``` 类的 run 方法实现了服务器的主要循环工作。当进入 <font color=blue>**LOOKING**</font> 状态，将会执行```org.apache.zookeeper.server.quorum.Election.lookForLeader```方法进行leader的选举，该方法返回前会将服务器状态设置为<font color=blue>**LEADING**</font> 或者<font color=blue>**FOLLOWING**</font> 状态，也可能是<font color=blue>**OBSERVING**</font>。
   

![](pictures/chapter9-2Zk选举过程.jpg)


