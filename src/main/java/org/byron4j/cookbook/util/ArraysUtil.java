package org.byron4j.cookbook.util;

import java.util.Random;

public class ArraysUtil {

    public static int[] generateRandomIntArray(int size, int bound){
        Random random = new Random();
        int[] result = new int[size];
        for( int i = 0; i < size; i ++ ){
            result[i] = random.nextInt(bound);
        }
        return  result;
    }

    public static void printArray(int[] arr){
        for( int ele : arr ){
            System.out.print("\t" + ele);
        }

        System.out.println();
    }
}
