package org.byron4j.cookbook.algrithms.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

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
        if(null == treeNode){
            return;
        }
        System.out.println(treeNode.data);
        preOrderTraveral(treeNode.leftChild);
        preOrderTraveral(treeNode.rightChild);
    }

    /**
     * 中序遍历
     * @param treeNode
     */
    public static void middleOrderTraveral(TreeNode treeNode){
        if(null == treeNode){
            return;
        }
        middleOrderTraveral(treeNode.leftChild);
        System.out.println(treeNode.data);
        middleOrderTraveral(treeNode.rightChild);
    }

    /**
     * 后序遍历
     * @param treeNode
     */
    public static void backOrderTraveral(TreeNode treeNode){
        if(null == treeNode){
            return;
        }
        backOrderTraveral(treeNode.leftChild);
        backOrderTraveral(treeNode.rightChild);
        System.out.println(treeNode.data);
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
        LinkedList linkedList = new LinkedList(Arrays.asList(1, 2, 3, null, null, 4, null, null, 5, 6, null, 7));
        TreeNode tree = createBinaryTree(linkedList);
        System.out.println("前序遍历：");
        preOrderTraveral(tree);
        System.out.println("中序遍历：");
        middleOrderTraveral(tree);
        System.out.println("后序遍历：");
        backOrderTraveral(tree);
    }
}

