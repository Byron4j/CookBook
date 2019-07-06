package org.byron4j.cookbook.jvm.chapte3;

/**
 -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=3145728
 */
public class PretenureSizeThresholdDemo {

    private static final int _1M = 1024 * 1024;
    public static void main(String[] args){
        byte[] arr = new byte[4 * _1M];
    }
}
