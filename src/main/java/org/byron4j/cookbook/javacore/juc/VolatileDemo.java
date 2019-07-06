package org.byron4j.cookbook.javacore.juc;

import org.byron4j.cookbook.util.Utils;

/**
 * stop 如果不是 volatile 修饰，则thread启动后，看到的是自己线程栈的变量值false，陷入死循环；
 * 如果被 volatile 修饰，则 stop 变量是线程可见的，stop 设置为true后，thread 会退出 while 循环
 */
public class VolatileDemo {
    volatile boolean stop = false;

    public static void main(String[] args) {
        // LoadMaker.makeLoad();

        VolatileDemo demo = new VolatileDemo();

        Thread thread = new Thread(demo.getConcurrencyCheckTask());
        thread.start();

        Utils.sleep(1000);
        System.out.println("Set stop to true in main!");
        demo.stop = true;
        System.out.println("Exit main.");
    }

    ConcurrencyCheckTask getConcurrencyCheckTask() {
        return new ConcurrencyCheckTask();
    }

    private class ConcurrencyCheckTask implements Runnable {
        @Override
        public void run() {
            System.out.println("ConcurrencyCheckTask started!");
            // 如果主线中stop的值可见，则循环会退出。
            // 在我的开发机上，几乎必现循环不退出！（简单安全的解法：在running属性上加上volatile）
            while (!stop) {
            }
            System.out.println("ConcurrencyCheckTask stopped!");
        }
    }
}
