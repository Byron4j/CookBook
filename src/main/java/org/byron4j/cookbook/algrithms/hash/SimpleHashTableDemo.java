package org.byron4j.cookbook.algrithms.hash;

import java.util.ArrayList;
import java.util.List;

/**
 * 拉链法哈希表最小示例（仅演示核心机制）。
 */
public class SimpleHashTableDemo {
    private static class Entry {
        String key;
        String value;
        Entry next;

        Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private final Entry[] table;

    public SimpleHashTableDemo(int capacity) {
        this.table = new Entry[capacity];
    }

    public void put(String key, String value) {
        int index = index(key);
        Entry head = table[index];
        Entry cursor = head;
        while (cursor != null) {
            if (cursor.key.equals(key)) {
                cursor.value = value;
                return;
            }
            cursor = cursor.next;
        }
        Entry entry = new Entry(key, value);
        entry.next = head;
        table[index] = entry;
    }

    public String get(String key) {
        int index = index(key);
        Entry cursor = table[index];
        while (cursor != null) {
            if (cursor.key.equals(key)) {
                return cursor.value;
            }
            cursor = cursor.next;
        }
        return null;
    }

    public List<String> keys() {
        List<String> result = new ArrayList<>();
        for (Entry bucket : table) {
            Entry cursor = bucket;
            while (cursor != null) {
                result.add(cursor.key);
                cursor = cursor.next;
            }
        }
        return result;
    }

    private int index(String key) {
        return (key.hashCode() & Integer.MAX_VALUE) % table.length;
    }

    public static void main(String[] args) {
        SimpleHashTableDemo hashTable = new SimpleHashTableDemo(8);
        hashTable.put("java", "语言");
        hashTable.put("redis", "缓存");
        hashTable.put("mq", "消息队列");
        hashTable.put("java", "编程语言");

        System.out.println("key=java -> " + hashTable.get("java"));
        System.out.println("key=redis -> " + hashTable.get("redis"));
        System.out.println("所有key -> " + hashTable.keys());
    }
}
