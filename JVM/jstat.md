```ps -elf | grep java```
0 S deploy   18816     1  0  80   0 - 1207894 futex_ May05 ?      00:12:17 java -Dfile.encoding=UTF-8 -jar services-order.jar
0 S deploy   24286     1  1  80   0 - 1215346 futex_ May06 ?      00:15:06 java -Dfile.encoding=UTF-8 -jar services-financial.jar
0 S deploy   29473 20655  0  80   0 - 25815 pipe_w 09:54 pts/2    00:00:00 grep java

```jstat -options```
-class
-compiler
-gc
-gccapacity
-gccause
-gcmetacapacity
-gcnew
-gcnewcapacity
-gcold
-gcoldcapacity
-gcutil
-printcompilation

```jstat -class 18816```

Loaded  Bytes  Unloaded  Bytes     Time
 13242 23616.6      508   681.4       7.68

```jstat -compiler 18816```

Compiled Failed Invalid   Time   FailedType FailedMethod
   18193      2       0    88.25          1 com/mysql/jdbc/AbandonedConnectionCleanupThread run

```jstat -gc 18816```

 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT
3072.0 3072.0 1511.8  0.0   125952.0 120490.7  123904.0   75581.4   79872.0 72868.7 9472.0 8389.4   1016    6.304   4      0.757    7.060

```jstat -gccapacity 18816```

 NGCMN    NGCMX     NGC     S0C   S1C       EC      OGCMN      OGCMX       OGC         OC       MCMN     MCMX      MC     CCSMN    CCSMX     CCSC    YGC    FGC
 20480.0 326656.0 132096.0 3072.0 3072.0 125952.0    40960.0   654336.0   123904.0   123904.0      0.0 1120256.0  79872.0      0.0 1048576.0   9472.0   1016     4

```jstat -gccause 18816```

  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT    LGCC                 GCC
 49.21   0.00  95.66  61.00  91.23  88.57   1016    6.304     4    0.757    7.060 Allocation Failure   No GC

```jstat -gcmetacapacity 18816```

   MCMN       MCMX        MC       CCSMN      CCSMX       CCSC     YGC   FGC    FGCT     GCT
       0.0  1120256.0    79872.0        0.0  1048576.0     9472.0  1016     4    0.757    7.060

```jstat -gcnew 18816```

 S0C    S1C    S0U    S1U   TT MTT  DSS      EC       EU     YGC     YGCT
3072.0 3072.0 1511.8    0.0 15  15 3072.0 125952.0 120490.7   1016    6.304

```jstat -gcnewcapacity 18816```

  NGCMN      NGCMX       NGC      S0CMX     S0C     S1CMX     S1C       ECMX        EC      YGC   FGC
   20480.0   326656.0   132096.0 108544.0   3072.0 108544.0   3072.0   325632.0   125952.0  1016     4

```jstat -gcold 18816```

   MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT
 79872.0  72868.7   9472.0   8389.4    123904.0     75581.4   1016     4    0.757    7.060

```jstat -gcoldcapacity 18816```

   OGCMN       OGCMX        OGC         OC       YGC   FGC    FGCT     GCT
    40960.0    654336.0    123904.0    123904.0  1016     4    0.757    7.060

```jstat -gcutil 18816```

  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT
 49.21   0.00  95.66  61.00  91.23  88.57   1016    6.304     4    0.757    7.060

```jstat -printcompilation 18816```

Compiled  Size  Type Method
   18193    669    1 com/alibaba/dubbo/remoting/exchange/support/header/HeaderExchangeChannel send

```jstat -printcompilation 18816```

Compiled  Size  Type Method
   18196     27    1 sun/security/util/ECUtil getECParameters
   
```jstat -printcompilation 18816```

Compiled  Size  Type Method
   18197    123    1 java/util/stream/Nodes$SpinedNodeBuilder accept