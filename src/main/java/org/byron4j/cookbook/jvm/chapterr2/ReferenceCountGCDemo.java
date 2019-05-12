package org.byron4j.cookbook.jvm.chapterr2;


/**
 * 使用虚拟机参数： -XX:+PrintGCDetails 输出信息：
 * <pre>
 *     [Full GC (System.gc()) [PSYoungGen: 5103K->0K(38400K)] [ParOldGen: 782K->5805K(87552K)] 5885K->5805K(125952K), [Metaspace: 3193K->3193K(1056768K)], 0.0083793 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
 * </pre>
 *
 * <pre>
 * 说明还是发生了GC，表明JVM不是采用引用计数法
 * </pre>
 */
public class ReferenceCountGCDemo {

    private Object instance = null;


    private static final int _1M = 1024 * 1024;
    private byte[] bigSize = new byte[ 2 * _1M];

    public static void main(String[] args){
        ReferenceCountGCDemo referenceCountGCDemo = new ReferenceCountGCDemo();
        ReferenceCountGCDemo referenceCountGCDemo1 = new ReferenceCountGCDemo();

        // 互相引用（如果采用引用计数则都为1此时）
        referenceCountGCDemo.instance = referenceCountGCDemo1;
        referenceCountGCDemo1.instance = referenceCountGCDemo;


        // GC
        System.gc();


    }
}
