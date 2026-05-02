package org.byron4j.cookbook.algrithms;

import org.byron4j.cookbook.algrithms.list.LinearListDemo;
import org.byron4j.cookbook.algrithms.tree.BinaryTree;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AlgorithmBasicsTest {

    @Test
    public void quickSortShouldSortNumbers() {
        int[] input = new int[]{9, 3, 5, 1, 8, 2, 7};
        QuickSort.sort(input);
        Assert.assertArrayEquals(new int[]{1, 2, 3, 5, 7, 8, 9}, input);
    }

    @Test
    public void linearListShouldSupportCrudOperations() {
        LinearListDemo list = new LinearListDemo(2);
        list.add(1);
        list.add(3);
        list.add(1, 2);
        int removed = list.removeAt(0);

        Assert.assertEquals(1, removed);
        Assert.assertEquals(2, list.size());
        Assert.assertArrayEquals(new int[]{2, 3}, list.toArray());
        Assert.assertEquals(0, list.indexOf(2));
    }

    @Test
    public void binaryTreeTraversalShouldMatchExpectedOrder() {
        LinkedList<Integer> source = new LinkedList<>(Arrays.asList(1, 2, 3, null, null, 4, null, null, 5, 6, null, 7));
        BinaryTree.TreeNode treeNode = BinaryTree.createBinaryTree(source);

        List<Integer> preOrder = BinaryTree.preOrderTraversalAsList(treeNode);
        List<Integer> inOrder = BinaryTree.middleOrderTraversalAsList(treeNode);
        List<Integer> postOrder = BinaryTree.backOrderTraversalAsList(treeNode);

        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7), preOrder);
        Assert.assertEquals(Arrays.asList(3, 2, 4, 1, 6, 7, 5), inOrder);
        Assert.assertEquals(Arrays.asList(3, 4, 2, 7, 6, 5, 1), postOrder);
    }
}
