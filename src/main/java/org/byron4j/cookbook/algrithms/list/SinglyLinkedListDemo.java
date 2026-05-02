package org.byron4j.cookbook.algrithms.list;

import java.util.StringJoiner;

/**
 * 单链表最小可运行示例。
 */
public class SinglyLinkedListDemo {
    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private int size;

    public void addLast(int value) {
        Node node = new Node(value);
        if (head == null) {
            head = node;
            size++;
            return;
        }
        Node cursor = head;
        while (cursor.next != null) {
            cursor = cursor.next;
        }
        cursor.next = node;
        size++;
    }

    public void addFirst(int value) {
        Node node = new Node(value);
        node.next = head;
        head = node;
        size++;
    }

    public boolean removeFirstValue(int value) {
        Node prev = null;
        Node cursor = head;
        while (cursor != null) {
            if (cursor.value == value) {
                if (prev == null) {
                    head = cursor.next;
                } else {
                    prev.next = cursor.next;
                }
                size--;
                return true;
            }
            prev = cursor;
            cursor = cursor.next;
        }
        return false;
    }

    public int indexOf(int value) {
        int index = 0;
        Node cursor = head;
        while (cursor != null) {
            if (cursor.value == value) {
                return index;
            }
            cursor = cursor.next;
            index++;
        }
        return -1;
    }

    public int size() {
        return size;
    }

    public String join() {
        StringJoiner joiner = new StringJoiner(" -> ");
        Node cursor = head;
        while (cursor != null) {
            joiner.add(String.valueOf(cursor.value));
            cursor = cursor.next;
        }
        return joiner.toString();
    }

    public static void main(String[] args) {
        SinglyLinkedListDemo list = new SinglyLinkedListDemo();
        list.addLast(10);
        list.addLast(20);
        list.addFirst(5);
        list.removeFirstValue(10);
        System.out.println("链表内容: " + list.join());
        System.out.println("元素20索引: " + list.indexOf(20));
        System.out.println("链表大小: " + list.size());
    }
}
