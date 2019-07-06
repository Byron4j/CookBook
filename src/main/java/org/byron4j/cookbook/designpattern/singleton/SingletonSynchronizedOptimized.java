package org.byron4j.cookbook.designpattern.singleton;

/**
 * 单例模式实例
 * 1. 构造器私有化
 * 2. 提供静态方法供外部获取单例实例
 * 3. 延迟初始化实例
 */
public class SingletonSynchronizedOptimized {

    private static SingletonSynchronizedOptimized instance;

    // 构造器私有化
    private SingletonSynchronizedOptimized(){

    }

    // 提供静态方法
    public static SingletonSynchronizedOptimized getInstance(){

        // 将同步锁范围缩小，降低性能损耗
        if(instance == null){
            synchronized (SingletonSynchronizedOptimized.class){
                if(instance == null){
                    instance = new SingletonSynchronizedOptimized();
                }
            }
        }
        return  instance;
    }


    public void display(){
        System.out.println("Hurray! I am create as a SingletonSynchronizedOptimized!");
    }


}
