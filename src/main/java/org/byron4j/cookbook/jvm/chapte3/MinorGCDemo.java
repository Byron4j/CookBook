package org.byron4j.cookbook.jvm.chapte3;


/**
 * 虚拟机配置参数：
 * -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
 * 环境：jdk1.8.0_202
 * <pre>
 * [GC (Allocation Failure) [PSYoungGen: 7535K->999K(9216K)] 7535K->3842K(19456K), 0.0021218 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 * 4194304
 * Heap
 *  PSYoungGen      total 9216K, used 5419K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
 *   eden space 8192K, 53% used [0x00000000ff600000,0x00000000ffa511a0,0x00000000ffe00000)
 *   from space 1024K, 97% used [0x00000000ffe00000,0x00000000ffef9c28,0x00000000fff00000)
 *   to   space 1024K, 0% used [0x00000000fff00000,0x00000000fff00000,0x0000000100000000)
 *  ParOldGen       total 10240K, used 6939K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
 *   object space 10240K, 67% used [0x00000000fec00000,0x00000000ff2c6c20,0x00000000ff600000)
 *  Metaspace       used 3202K, capacity 4496K, committed 4864K, reserved 1056768K
 *   class space    used 345K, capacity 388K, committed 512K, reserved 1048576K
 *
 * </pre>
 */
public class MinorGCDemo {
    private static final int _1M = 1024 * 1024;
    public static void main(String[] args){
        byte[] arr1, arr2, arr3, arr4;
        arr1 = new byte[2 * _1M];
        arr2 = new byte[2 * _1M];
        arr3 = new byte[2 * _1M];
        arr4 = new byte[4 * _1M];  //

    }
}
