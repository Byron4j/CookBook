package org.byron4j.cookbook.algrithms;

import org.byron4j.cookbook.util.ArraysUtil;

public class QuickSort {
    /**
     * 排序的核心算法
     *
     * @param array
     *      待排序数组
     * @param startIndex
     *      开始位置
     * @param endIndex
     *      结束位置
     */
    public static void sortCore(int[] array, int startIndex, int endIndex) {
        if (startIndex >= endIndex) {
            return;
        }

        int boundary = boundary(array, startIndex, endIndex);

        sortCore(array, startIndex, boundary - 1);
        sortCore(array, boundary + 1, endIndex);
    }

    /**
     * 交换并返回分界点
     *
     * @param array
     *      待排序数组
     * @param startIndex
     *      开始位置
     * @param endIndex
     *      结束位置
     * @return
     *      分界点
     */
    private static int boundary(int[] array, int startIndex, int endIndex) {
        int standard = array[startIndex]; // 定义标准
        int leftIndex = startIndex; // 左指针
        int rightIndex = endIndex; // 右指针

        while(leftIndex < rightIndex) {
            while(leftIndex < rightIndex && array[rightIndex] >= standard) {
                rightIndex--;
            }
            array[leftIndex] = array[rightIndex];

            while(leftIndex < rightIndex && array[leftIndex] <= standard) {
                leftIndex++;
            }
            array[rightIndex] = array[leftIndex];
        }

        array[leftIndex] = standard;
        return leftIndex;
    }


    public static void main(String[] args){
        int[] arr = ArraysUtil.generateRandomIntArray(10, 100);
        ArraysUtil.printArray(arr);
        sortCore(arr, 0, arr.length - 1);
        ArraysUtil.printArray(arr);
    }

}
