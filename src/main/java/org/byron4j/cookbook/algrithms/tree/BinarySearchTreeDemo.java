package org.byron4j.cookbook.algrithms.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * 二叉查找树最小可运行示例。
 */
public class BinarySearchTreeDemo {
    private static class Node {
        int value;
        Node left;
        Node right;

        Node(int value) {
            this.value = value;
        }
    }

    private Node root;

    public void insert(int value) {
        root = insert(root, value);
    }

    private Node insert(Node node, int value) {
        if (node == null) {
            return new Node(value);
        }
        if (value < node.value) {
            node.left = insert(node.left, value);
        } else if (value > node.value) {
            node.right = insert(node.right, value);
        }
        return node;
    }

    public boolean contains(int value) {
        Node cursor = root;
        while (cursor != null) {
            if (value == cursor.value) {
                return true;
            }
            cursor = value < cursor.value ? cursor.left : cursor.right;
        }
        return false;
    }

    public List<Integer> inOrder() {
        List<Integer> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(Node node, List<Integer> result) {
        if (node == null) {
            return;
        }
        inOrder(node.left, result);
        result.add(node.value);
        inOrder(node.right, result);
    }

    public static void main(String[] args) {
        BinarySearchTreeDemo bst = new BinarySearchTreeDemo();
        int[] values = new int[]{8, 3, 10, 1, 6, 14, 4, 7, 13};
        for (int value : values) {
            bst.insert(value);
        }
        System.out.println("中序遍历: " + bst.inOrder());
        System.out.println("是否包含7: " + bst.contains(7));
        System.out.println("是否包含2: " + bst.contains(2));
    }
}
