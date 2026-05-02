package org.byron4j.cookbook.algrithms.tree;

import java.util.Arrays;

/**
 * B 树查询过程最小示例（手工构造一棵 3 阶 B 树用于演示查询）。
 */
public class BTreeSearchDemo {
    private static class Node {
        int[] keys;
        Node[] children;
        boolean leaf;

        Node(int[] keys, Node[] children, boolean leaf) {
            this.keys = keys;
            this.children = children;
            this.leaf = leaf;
        }
    }

    private final Node root;

    public BTreeSearchDemo() {
        Node left = new Node(new int[]{5, 10}, null, true);
        Node middle = new Node(new int[]{20, 25}, null, true);
        Node right = new Node(new int[]{35, 40, 45}, null, true);
        this.root = new Node(new int[]{15, 30}, new Node[]{left, middle, right}, false);
    }

    public boolean contains(int target) {
        return contains(root, target);
    }

    private boolean contains(Node node, int target) {
        int i = 0;
        while (i < node.keys.length && target > node.keys[i]) {
            i++;
        }
        if (i < node.keys.length && target == node.keys[i]) {
            return true;
        }
        if (node.leaf) {
            return false;
        }
        return contains(node.children[i], target);
    }

    public static void main(String[] args) {
        BTreeSearchDemo bTree = new BTreeSearchDemo();
        System.out.println("根节点keys: " + Arrays.toString(bTree.root.keys));
        System.out.println("contains(25): " + bTree.contains(25));
        System.out.println("contains(18): " + bTree.contains(18));
    }
}
