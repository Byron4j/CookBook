package org.byron4j.cookbook.designpattern;

import org.byron4j.cookbook.designpattern.observer.*;
import org.junit.Test;

public class ObserverTest {

    @Test
    public void test(){

        // 主体
        Subject publisher = Publisher.builder().build();
        publisher.initObservers();

        // 观察者注册
        Observer subscriber1 = Subscriber1.builder().build();
        publisher.registerObserver(subscriber1);

        Observer subscriber2= Subscriber2.builder().build();
        publisher.registerObserver(subscriber2);

        // 通知当前已注册的观察者
        publisher.notifyObservers();

        // 移除观察者
        publisher.removeObserver(subscriber2);

        // 通知当前已注册的观察者
        publisher.notifyObservers();
    }


    @Test
    public void test2WeakReference(){

        // 主体
        Subject publisher = Publisher2WeakReference.builder().build();
        publisher.initObservers();

        // 观察者注册
        Observer subscriber1 = Subscriber1.builder().build();
        publisher.registerObserver(subscriber1);

        Observer subscriber2= Subscriber2.builder().build();
        publisher.registerObserver(subscriber2);

        // 通知当前已注册的观察者
        publisher.notifyObservers();

        // 移除观察者
        publisher.removeObserver(subscriber2);

        // 通知当前已注册的观察者
        publisher.notifyObservers();
    }




}
