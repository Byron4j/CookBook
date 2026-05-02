package org.byron4j.cookbook.algrithms.tree;

import java.util.Map;
import java.util.TreeMap;

/**
 * 用 TreeMap 演示红黑树特性（TreeMap 底层为红黑树）。
 */
public class TreeMapRedBlackTreeDemo {
    public static void main(String[] args) {
        TreeMap<Integer, String> treeMap = new TreeMap<>();
        treeMap.put(5, "E");
        treeMap.put(2, "B");
        treeMap.put(8, "H");
        treeMap.put(1, "A");
        treeMap.put(3, "C");

        System.out.println("有序遍历结果:");
        for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        System.out.println("firstKey: " + treeMap.firstKey());
        System.out.println("lastKey: " + treeMap.lastKey());
        System.out.println("ceilingKey(4): " + treeMap.ceilingKey(4));
    }
}
