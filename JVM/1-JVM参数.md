# JVM参数


- -server -Xms8g -Xmx8g -Xmn4g
    - 以上是RocketMQ默认配置，表示： JVM初始分配的内存为8g、JVM最大分配内存为8g、JVM最小分配内存
    - JVM初始分配的内存由```-Xms```指定，默认是物理内存的1/64；JVM最大分配的内存由-Xmx指 定，默认是物理内存的1/4。默认空余堆内存小于40%时，JVM就会增大堆直到-Xmx的最大限制；空余堆内存大于70%时，JVM会减少堆直到 -Xms的最小限制。因此服务器一般设置-Xms、-Xmx相等以避免在每次GC 后调整堆的大小。对象的堆内存由称为垃圾回收器的自动内存管理系统回收。

- -XX:+AlwaysPreTouch
    - 在JVM初始化时就尝试接触以分配堆空间，这会延长启动时间

- -XX:-UseBiasedLocking
    - 禁用偏向锁，可以减少JVM挂起时间
    
- -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercent=25 -XX:InitiatingHeapOccupancyPercent=30
    - 使用JDK8的的G1垃圾收集器


# 参考资料

- [偏向锁](https://www.sogou.com/link?url=DOb0bgH2eKh1ibpaMGjuy905KHxwxXOYdv1q5jK9p-tRa6NQaxCrEpFZhCcrseC8MZuZ9waVJJw.)
- [RocketMQ中的JVM参数调优](http://rocketmq.apache.org/docs/system-config/)