package org.byron4j.cookbook.designpattern.strategy;

import java.util.Arrays;

public class InsertionSort implements SortingStrategy {

    @Override
    public void sort(int[] numbers) {
        System.out.println("Insertion Sort!");

        for (int i = 1; i < numbers.length; i++) {
            int temp = numbers[i];
            int j;
            for (j = i - 1; (j >= 0) && (numbers[j] > temp); j--) {
                numbers[j + 1] = numbers[j];
            }
            numbers[j + 1] = temp;
        }

        System.out.println(Arrays.toString(numbers));
    }
}