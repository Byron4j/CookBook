package org.byron4j.cookbook.java8.basic;

import org.junit.jupiter.api.Test;

/**
 * 比较java8与之前的编码方式
 */
public class CompareJava8AndPreVersion {

    @Test
    public void test(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Java8之前的编码方式.");
            }
        };

        Runnable runnable4Java8 = () -> {
            System.out.println("Java8编码方式.");
        };
    }

}
