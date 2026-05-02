package org.byron4j.cookbook.algrithms.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 二叉树遍历实例--深度遍历：
 * 前序遍历
 * 中序遍历
 * 后序遍历
 */
public class BinaryTree {

    public static class TreeNode{
        int data;
        TreeNode leftChild;
        TreeNode rightChild;

        public TreeNode(int data) {
            this.data = data;
        }
    }


    /**
     * 前序遍历
     * @param treeNode
     */
    public static void preOrderTraveral(TreeNode treeNode){
        for (Integer value : preOrderTraversalAsList(treeNode)) {
            System.out.println(value);
        }
    }

    public static List<Integer> preOrderTraversalAsList(TreeNode treeNode) {
        List<Integer> result = new ArrayList<>();
        preOrder(treeNode, result);
        return result;
    }

    private static void preOrder(TreeNode treeNode, List<Integer> result) {
        if(null == treeNode){
            return;
        }
        result.add(treeNode.data);
        preOrder(treeNode.leftChild, result);
        preOrder(treeNode.rightChild, result);
    }

    /**
     * 中序遍历
     * @param treeNode
     */
    public static void middleOrderTraveral(TreeNode treeNode){
        for (Integer value : middleOrderTraversalAsList(treeNode)) {
            System.out.println(value);
        }
    }

    public static List<Integer> middleOrderTraversalAsList(TreeNode treeNode) {
        List<Integer> result = new ArrayList<>();
        middleOrder(treeNode, result);
        return result;
    }

    private static void middleOrder(TreeNode treeNode, List<Integer> result) {
        if(null == treeNode){
            return;
        }
        middleOrder(treeNode.leftChild, result);
        result.add(treeNode.data);
        middleOrder(treeNode.rightChild, result);
    }

    /**
     * 后序遍历
     * @param treeNode
     */
    public static void backOrderTraveral(TreeNode treeNode){
        for (Integer value : backOrderTraversalAsList(treeNode)) {
            System.out.println(value);
        }
    }

    public static List<Integer> backOrderTraversalAsList(TreeNode treeNode) {
        List<Integer> result = new ArrayList<>();
        backOrder(treeNode, result);
        return result;
    }

    private static void backOrder(TreeNode treeNode, List<Integer> result) {
        if(null == treeNode){
            return;
        }
        backOrder(treeNode.leftChild, result);
        backOrder(treeNode.rightChild, result);
        result.add(treeNode.data);
    }

    /**
     * 使用提供的链表创建二叉树
     * @param linkedList
     * @return
     */
    public static TreeNode createBinaryTree(LinkedList<Integer> linkedList){
        TreeNode node = null;
        if(null == linkedList || linkedList.isEmpty()){
            return null;
        }

        // 移除首节点并返回
        Integer data = linkedList.removeFirst();
        if( null != data ){
            node = new TreeNode(data);
            node.leftChild = createBinaryTree(linkedList);
            node.rightChild = createBinaryTree(linkedList);

        }
        return node;
    }


    public static void main(String[] args) {
        LinkedList<Integer> linkedList = new LinkedList<>(Arrays.asList(1, 2, 3, null, null, 4, null, null, 5, 6, null, 7));
        TreeNode tree = createBinaryTree(linkedList);
        System.out.println("前序遍历：");
        preOrderTraveral(tree);
        System.out.println("中序遍历：");
        middleOrderTraveral(tree);
        System.out.println("后序遍历：");
        backOrderTraveral(tree);
    }
}

