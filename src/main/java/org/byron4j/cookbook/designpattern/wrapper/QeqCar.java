package org.byron4j.cookbook.designpattern.wrapper;

/**
 * @program: cookbook
 * @author: Byron
 * @create: 2019/07/29
 */
public class QeqCar implements Car {
    @Override
    public void run() {
        System.out.println("Qeq go...");
    }

    @Override
    public void stop() {
        System.out.println("Qeq stop!");
    }
}
