package org.byron4j.cookbook.designpattern.wrapper;

/**
 * @program: cookbook
 * @author: Byron
 * @create: 2019/07/29
 */
public class WrapperCar implements Car {

    private Car car;

    public WrapperCar(Car car) {
        this.car = car;
    }

    @Override
    public void run() {
        System.out.println("wapper run...");
    }

    @Override
    public void stop() {
        car.stop();
    }
}
