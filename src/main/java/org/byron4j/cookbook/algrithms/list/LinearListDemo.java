package org.byron4j.cookbook.algrithms.list;

import java.util.Arrays;

/**
 * 顺序表（线性表）最小可运行示例。
 */
public class LinearListDemo {
    private int[] elements;
    private int size;

    public LinearListDemo(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.elements = new int[capacity];
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public int get(int index) {
        checkIndex(index);
        return elements[index];
    }

    public void add(int value) {
        ensureCapacity(size + 1);
        elements[size++] = value;
    }

    public void add(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index out of bound: " + index);
        }
        ensureCapacity(size + 1);
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = value;
        size++;
    }

    public int removeAt(int index) {
        checkIndex(index);
        int removed = elements[index];
        if (index < size - 1) {
            System.arraycopy(elements, index + 1, elements, index, size - index - 1);
        }
        size--;
        return removed;
    }

    public int indexOf(int value) {
        for (int i = 0; i < size; i++) {
            if (elements[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public int[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    private void ensureCapacity(int targetSize) {
        if (targetSize <= elements.length) {
            return;
        }
        int newCapacity = Math.max(elements.length * 2, targetSize);
        elements = Arrays.copyOf(elements, newCapacity);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index out of bound: " + index);
        }
    }

    public static void main(String[] args) {
        LinearListDemo list = new LinearListDemo(4);
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(1, 15);
        list.removeAt(2);
        System.out.println("线性表结果: " + Arrays.toString(list.toArray()));
        System.out.println("15 的索引: " + list.indexOf(15));
    }
}
