package org.byron4j.cookbook.algrithms;

import org.byron4j.cookbook.algrithms.hash.SimpleHashTableDemo;
import org.byron4j.cookbook.algrithms.list.SinglyLinkedListDemo;
import org.byron4j.cookbook.algrithms.tree.AvlTreeDemo;
import org.byron4j.cookbook.algrithms.tree.BTreeSearchDemo;
import org.byron4j.cookbook.algrithms.tree.BinarySearchTreeDemo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class DataStructureCompletenessTest {

    @Test
    public void linkedListShouldWork() {
        SinglyLinkedListDemo list = new SinglyLinkedListDemo();
        list.addFirst(2);
        list.addFirst(1);
        list.addLast(3);

        Assert.assertEquals("1 -> 2 -> 3", list.join());
        Assert.assertTrue(list.removeFirstValue(2));
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(1, list.indexOf(3));
    }

    @Test
    public void hashTableShouldSupportPutAndGet() {
        SimpleHashTableDemo hashTable = new SimpleHashTableDemo(4);
        hashTable.put("A", "1");
        hashTable.put("B", "2");
        hashTable.put("A", "11");

        Assert.assertEquals("11", hashTable.get("A"));
        Assert.assertEquals("2", hashTable.get("B"));
        Assert.assertNull(hashTable.get("C"));
    }

    @Test
    public void bstShouldProduceSortedOrder() {
        BinarySearchTreeDemo bst = new BinarySearchTreeDemo();
        int[] values = new int[]{5, 3, 8, 1, 4, 7, 9};
        for (int value : values) {
            bst.insert(value);
        }

        Assert.assertEquals(Arrays.asList(1, 3, 4, 5, 7, 8, 9), bst.inOrder());
        Assert.assertTrue(bst.contains(7));
        Assert.assertFalse(bst.contains(2));
    }

    @Test
    public void avlShouldRemainOrderedAfterRotations() {
        AvlTreeDemo avl = new AvlTreeDemo();
        int[] values = new int[]{30, 20, 10, 25, 40, 50, 22};
        for (int value : values) {
            avl.insert(value);
        }
        Assert.assertEquals(Arrays.asList(10, 20, 22, 25, 30, 40, 50), avl.inOrder());
    }

    @Test
    public void bTreeSearchShouldReturnExpectedResult() {
        BTreeSearchDemo bTree = new BTreeSearchDemo();
        Assert.assertTrue(bTree.contains(25));
        Assert.assertFalse(bTree.contains(18));
    }
}
