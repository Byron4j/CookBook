package org.byron4j.cookbook.designpattern;

import org.byron4j.cookbook.designpattern.singleton.Singleton;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingletonTest {

    @Test
    public void test(){
        final Set<Singleton> sets = new HashSet<>();

        ExecutorService es = Executors.newFixedThreadPool(10000);

        for(int i = 1; i <= 100000; i++){
            es.execute(new Runnable() {
                public  void run(){
                    Singleton s = Singleton.getInstance();
                    sets.add(s);
                }
            });
        }

        System.out.println(sets);

    }
}
