package org.byron4j.cookbook.algrithms.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * AVL 树最小可运行示例（插入 + 平衡 + 中序遍历）。
 */
public class AvlTreeDemo {
    private static class Node {
        int value;
        int height = 1;
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
        } else {
            return node;
        }

        updateHeight(node);
        int balance = getBalance(node);

        if (balance > 1 && value < node.left.value) {
            return rotateRight(node);
        }
        if (balance < -1 && value > node.right.value) {
            return rotateLeft(node);
        }
        if (balance > 1 && value > node.left.value) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1 && value < node.right.value) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    private Node rotateLeft(Node node) {
        Node newRoot = node.right;
        Node transfer = newRoot.left;
        newRoot.left = node;
        node.right = transfer;
        updateHeight(node);
        updateHeight(newRoot);
        return newRoot;
    }

    private Node rotateRight(Node node) {
        Node newRoot = node.left;
        Node transfer = newRoot.right;
        newRoot.right = node;
        node.left = transfer;
        updateHeight(node);
        updateHeight(newRoot);
        return newRoot;
    }

    private void updateHeight(Node node) {
        node.height = Math.max(height(node.left), height(node.right)) + 1;
    }

    private int getBalance(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private int height(Node node) {
        return node == null ? 0 : node.height;
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
        AvlTreeDemo avl = new AvlTreeDemo();
        int[] values = new int[]{30, 20, 10, 25, 40, 50, 22};
        for (int value : values) {
            avl.insert(value);
        }
        System.out.println("AVL中序遍历: " + avl.inOrder());
    }
}
