package org.byron4j.cookbook.algrithms;

import org.junit.Test;

public class QuickSortTest {
    @Test
    public void test(){
        int[] arr = new int[]{6, 2, 4, 1, 5, 9};
        QuickSort.sortCore(arr, 0, 5);
        for (int i : arr) {
            System.out.println(i);
        }
    }
}
