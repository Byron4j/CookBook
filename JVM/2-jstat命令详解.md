# jstat命令详解

预设前提：  **堆内存划分为 ```Eden```、```Survivor``` 和 ```Tenured/Old``` 空间**。

## jstat -options 列出当前JVM版本可选项

```shell
jstat -options 
```

- -class ： 显示类加载情况
- -compiler ： 显示JVM实时编译信息
- -gc ： gc堆状态
- -gccapacity ： gc分区大小
- -gccause ： 最近一次gc统计和原因
- -gcmetacapacity ： gc元空间大小
- -gcnew ： 新区gc统计
- -gcnewcapacity ： 新区分区大小
- -gcold ： 老区统计
- -gcoldcapacity ： 老区大小
- -gcutil ： gc统计汇总
- -printcompilation ： 最近编译方法统计


## jstat -options <pid> 查看java进程id的情况



### jstat -class 18816  显示类加载情况

```jstat -class 18816```

|Loaded|Bytes|Unloaded|Bytes|Time|
|---|---|---|---|---|
|13242|23616.6|508|681.4|7.68|
           
                  
 
- Loaded ： 已加载的class数量
- Bytes ： 所占字节大小
- Unloaded ： 未加载的class数量
- Bytes ： 所占字节大小
- Time ： 时间ms


### jstat -compiler 18816  显示JVM实时编译信息

```jstat -compiler 18816```

|Compiled|Failed|Invalid|Time|FailedType|FailedMethod|
|---|---|---|---|---|---|
|18193|2|0|88.25|1|com/mysql/jdbc/AbandonedConnectionCleanupThread run|


### jstat -gc 18816  显示gc堆状态

```jstat -gc 18816```

|S0C|    S1C |   S0U  |  S1U   |   EC  |     EU   |     OC   |      OU    |   MC  |   MU |   CCSC|   CCSU|   YGC|     YGCT  |  FGC |   FGCT |    GCT|
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
|3072.0 |3072.0 |1511.8 | 0.0  | 125952.0 |120490.7 | 123904.0 |  75581.4 |  79872.0| 72868.7 |9472.0 |8389.4 |  1016 |   6.304 |  4  |    0.757 |   7.060|

- S0C：第一个幸存区survivor的大小
- S1C：第二个幸存区survivor的大小
- S0U：第一个幸存区survivor的使用大小
- S1U：第二个幸存区survivor的使用大小
- EC：伊甸园区eden的大小
- EU：伊甸园区eden的使用大小
- OC：老年代old大小
- OU：老年代old使用大小
- MC：方法区大小
- MU：方法区使用大小
- CCSC:压缩类空间大小
- CCSU:压缩类空间使用大小
- YGC：年轻代垃圾回收次数
- YGCT：年轻代垃圾回收消耗时间
- FGC：老年代垃圾回收次数
- FGCT：老年代垃圾回收消耗时间
- GCT：垃圾回收消耗总时间

### jstat -gccapacity 18816  显示gc分区大小

```jstat -gccapacity 18816```

|NGCMN|    NGCMX |   NGC  |  S0C   |   S1C  |     EC   |     OGCMN   |      OGCMX    |   OGC  |   OC |   MCMN|   MCMX|   MC|     CCSMN  |  CCSMX |   CCSC |    YGC|FGC|
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
|20480.0 |326656.0 |132096.0 | 3072.0  | 3072.0 |125952.0 | 40960.0 |  654336.0 |  123904.0| 123904.0 |0.0 |1120256.0 |  79872.0  |   0.0 |  1048576.0  |    9472.0 | 1016|4|


- NGCMN：新生代最小容量
- NGCMX：新生代最大容量
- NGC：当前新生代容量
- S0C：第一个幸存区大小
- S1C：第二个幸存区的大小
- EC：伊甸园区的大小
- OGCMN：老年代最小容量
- OGCMX：老年代最大容量
- OGC：当前老年代大小
- OC:当前老年代大小
- MCMN:最小元数据容量
- MCMX：最大元数据容量
- MC：当前元数据空间大小
- CCSMN：最小压缩类空间大小
- CCSMX：最大压缩类空间大小
- CCSC：当前压缩类空间大小
- YGC：年轻代gc次数
- FGC：老年代GC次数

### jstat -gccause 18816  显示最近一次gc统计和原因


```jstat -gccause 18816```

 | S0 |    S1  |   E   |   O    |  M     |CCS  |  YGC  |   YGCT|    FGC   | FGCT  |   GCT |   LGCC  |               GCC|
 |---|---|---|---|---|---|---|---|---|---|---|---|---|
 |49.21|   0.00 | 95.66|  61.00  |91.23|  88.57 |  1016  |  6.304 |    4 |   0.757 |   7.060| Allocation Failure |  No GC|


- LGCC：最后一次GC原因
- GCC：当前GC原因（No GC 为当前没有执行GC）

### jstat -gcmetacapacity 18816  显示gc元空间大小

```jstat -gcmetacapacity 18816```

   |MCMN  |     MCMX   |     MC   |    CCSMN  |    CCSMX   |    CCSC  |   YGC  | FGC |   FGCT  |   GCT|
   |---|---|---|---|---|---|---|---|---|---|
     |   0.0 |  1120256.0  |   79872.0   |      0.0 |  1048576.0   |   9472.0 |  1016  |    4  |   0.757  |   7.060 |
     
- MCMN:最小元数据容量
- MCMX：最大元数据容量
- MC：当前元数据空间大小
- CCSMN：最小压缩类空间大小
- CCSMX：最大压缩类空间大小
- CCSC：当前压缩类空间大小
- YGC：年轻代垃圾回收次数
- FGC：老年代垃圾回收次数
- FGCT：老年代垃圾回收消耗时间
- GCT：垃圾回收消耗总时间     

### jstat -gcnew 18816  显示新区gc统计

```jstat -gcnew 18816```

 |S0C  |  S1C  |  S0U  |  S1U  | TT| MTT | DSS   |   EC   |    EU   |  YGC  |   YGCT|
 |---|---|---|---|---|---|---|---|---|---|---|
|3072.0| 3072.0| 1511.8 |   0.0| 15|  15| 3072.0| 125952.0 |120490.7 |  1016 |   6.304|

- S0C：第一个幸存区大小
- S1C：第二个幸存区的大小
- S0U：第一个幸存区的使用大小
- S1U：第二个幸存区的使用大小
- TT:对象在新生代存活的次数
- MTT:对象在新生代存活的最大次数
- DSS:期望的幸存区大小
- EC：伊甸园区的大小
- EU：伊甸园区的使用大小
- YGC：年轻代垃圾回收次数
- YGCT：年轻代垃圾回收消耗时间

### jstat -gcnewcapacity 18816  显示新区分区大小

```jstat -gcnewcapacity 18816```

  |NGCMN|      NGCMX  |     NGC  |    S0CMX |    S0C   |  S1CMX   |  S1C   |    ECMX |       EC  |    YGC |  FGC|
  |---|---|---|---|---|---|---|---|---|---|---|
   |20480.0 |  326656.0 |  132096.0 |108544.0 |  3072.0 |108544.0 |  3072.0|   325632.0 |  125952.0 | 1016  |   4|
   
- NGCMN：新生代最小容量
- NGCMX：新生代最大容量
- NGC：当前新生代容量
- S0CMX：最大幸存1区大小
- S0C：当前幸存1区大小
- S1CMX：最大幸存2区大小
- S1C：当前幸存2区大小
- ECMX：最大伊甸园区大小
- EC：当前伊甸园区大小
- YGC：年轻代垃圾回收次数
- FGC：老年代回收次数   

### jstat -gcold 18816  显示老区统计

```jstat -gcold 18816```

   |MC |      MU |     CCSC  |   CCSU   |    OC   |       OU   |    YGC |   FGC |   FGCT |    GCT|
   |---|---|---|---|---|---|---|---|---|---|
 |79872.0  |72868.7 |  9472.0|   8389.4|    123904.0 |    75581.4 |  1016  |   4  |  0.757 |   7.060|
 
- MC：方法区大小
- MU：方法区使用大小
- CCSC:压缩类空间大小
- CCSU:压缩类空间使用大小
- OC：老年代大小
- OU：老年代使用大小
- YGC：年轻代垃圾回收次数
- FGC：老年代垃圾回收次数
- FGCT：老年代垃圾回收消耗时间
- GCT：垃圾回收消耗总时间 

### jstat -gcoldcapacity 18816  显示老区大小

```jstat -gcoldcapacity 18816```

   |OGCMN   |    OGCMX   |     OGC     |    OC    |   YGC|   FGC |   FGCT  |   GCT|
   |---|---|---|---|---|---|---|---|
   | 40960.0 |   654336.0 |   123904.0   | 123904.0  |1016  |   4  |  0.757  |  7.060|
   
- OGCMN：老年代最小容量
- OGCMX：老年代最大容量
- OGC：当前老年代大小
- OC：老年代大小
- YGC：年轻代垃圾回收次数
- FGC：老年代垃圾回收次数
- FGCT：老年代垃圾回收消耗时间
- GCT：垃圾回收消耗总时间   

### jstat -gcutil 18816  显示gc统计汇总

```jstat -gcutil 18816```

  |S0   |  S1  |   E    |  O   |   M   |  CCS |   YGC  |   YGCT  |  FGC  |  FGCT  |   GCT|
  |---|---|---|---|---|---|---|---|---|---|---|
 |49.21 |  0.00|  95.66 | 61.00 | 91.23 | 88.57  | 1016  |  6.304 |    4 |   0.757 |   7.060|
 
- S0：幸存1区当前使用比例
- S1：幸存2区当前使用比例
- E：伊甸园区使用比例
- O：老年代使用比例
- M：元数据区使用比例
- CCS：压缩使用比例
- YGC：年轻代垃圾回收次数
- FGC：老年代垃圾回收次数
- FGCT：老年代垃圾回收消耗时间
- GCT：垃圾回收消耗总时间 

### jstat -printcompilation 18816  显示最近编译方法统计

```jstat -printcompilation 18816```

|Compiled | Size|  Type| Method|
|---|---|---|---|
  | 18193 |   669 |   1 |com/alibaba/dubbo/remoting/exchange/support/header/HeaderExchangeChannel send|
 |  18196    | 27   | 1 |sun/security/util/ECUtil getECParameters|
  | 18197  |  123   | 1 |java/util/stream/Nodes$SpinedNodeBuilder accept|
  
- Compiled：最近编译方法的数量
- Size：最近编译方法的字节码数量
- Type：最近编译方法的编译类型。
- Method：方法名标识  
   
   
  
#### 可能碰到的问题

- [jstat Could not attach to $pid](https://stackoverflow.com/questions/15401937/can-not-take-jstat-metrics-using-bash-as-sensu-plugin)  
   
#### 参考资料

- [https://www.cnblogs.com/lizhonghua34/p/7280699.html](https://www.cnblogs.com/lizhonghua34/p/7280699.html)  
- [https://www.cnblogs.com/lizhonghua34/p/7307139.html](https://www.cnblogs.com/lizhonghua34/p/7307139.html) 