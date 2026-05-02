package org.byron4j.cookbook.algrithms.stackqueue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 栈与队列最小可运行示例。
 */
public class StackQueueDemo {
    public static void main(String[] args) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        int stackTop = stack.pop();

        Queue<String> queue = new LinkedList<>();
        queue.offer("A");
        queue.offer("B");
        queue.offer("C");
        String queueHead = queue.poll();

        System.out.println("栈弹出元素: " + stackTop + ", 当前栈顶: " + stack.peek());
        System.out.println("队列出队元素: " + queueHead + ", 当前队头: " + queue.peek());
    }
}
