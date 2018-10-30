package org.byron4j.cookbook.designpattern;

import org.byron4j.cookbook.designpattern.singleton.*;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingletonTest {

    @Test
    public void test(){
        final Set<Singleton> sets = new HashSet<>();
        long startTime = System.currentTimeMillis();
        ExecutorService es = Executors.newFixedThreadPool(10000);

        for(int i = 1; i <= 100000; i++){
            es.execute(new Runnable() {
                public  void run(){
                    Singleton s = Singleton.getInstance();
                    sets.add(s);
                }
            });
        }
        System.out.println("test用时:" + (System.currentTimeMillis() - startTime));
        System.out.println(sets);

    }

    @Test
    public void testSynchronized(){
        final Set<SingletonSynchronized> sets = new HashSet<>();
        long startTime = System.currentTimeMillis();
        ExecutorService es = Executors.newFixedThreadPool(10000);

        for(int i = 1; i <= 100000; i++){
            es.execute(new Runnable() {
                public  void run(){
                    SingletonSynchronized s = SingletonSynchronized.getInstance();
                    sets.add(s);
                }
            });
        }
        System.out.println("testSynchronized用时:" + (System.currentTimeMillis() - startTime));
        System.out.println(sets);

    }

    @Test
    public void testOptimised(){
        final Set<SingletonSynchronizedOptimized> sets = new HashSet<>();
        long startTime = System.currentTimeMillis();
        ExecutorService es = Executors.newFixedThreadPool(10000);

        for(int i = 1; i <= 100000; i++){
            es.execute(new Runnable() {
                public  void run(){
                    SingletonSynchronizedOptimized s = SingletonSynchronizedOptimized.getInstance();
                    sets.add(s);
                }
            });
        }

        System.out.println("testOptimised用时:" + (System.currentTimeMillis() - startTime));
        System.out.println(sets);

    }

    @Test
    public void testClone() throws CloneNotSupportedException, Exception {
        SingletonZClone singletonZClone1 = SingletonZClone.getInstance();
        SingletonZClone singletonZClone2 = SingletonZClone.getInstance();
        SingletonZClone singletonZClone3 = (SingletonZClone)SingletonZClone.getInstance().clone();

        System.out.println(singletonZClone1 == singletonZClone2);
        System.out.println(singletonZClone1 == singletonZClone3);
        System.out.println(singletonZClone2 == singletonZClone3);



        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("test.ser"));
        oos.writeObject(singletonZClone1);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("test.ser"));
        SingletonZClone test = (SingletonZClone) ois.readObject();
        ois.close();
        System.out.println(singletonZClone1 == test);

    }

    @Test
    public void testSeria() throws Exception {
        SingletonZCloneSerializable singletonZClone1 = SingletonZCloneSerializable.getInstance();


        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("test.ser"));
        oos.writeObject(singletonZClone1);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("test.ser"));
        SingletonZCloneSerializable test = (SingletonZCloneSerializable) ois.readObject();
        ois.close();
        System.out.println(singletonZClone1 == test);

    }

    @Test
    public void testSReadResolve() throws Exception {

        SingletonZCloneSerializableReadResolve s = SingletonZCloneSerializableReadResolve.getInstance();


        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("test.ser"));
        oos.writeObject(s);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("test.ser"));
        SingletonZCloneSerializableReadResolve test = (SingletonZCloneSerializableReadResolve) ois.readObject();
        ois.close();
        System.out.println(s == test);

    }

}
