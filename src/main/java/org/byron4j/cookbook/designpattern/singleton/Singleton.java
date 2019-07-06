package org.byron4j.cookbook.designpattern.singleton;

/**
 * 单例模式实例
 * 1. 构造器私有化
 * 2. 提供静态方法供外部获取单例实例
 * 3. 延迟初始化实例
 */
public class Singleton {

    private static Singleton instance;

    // 构造器私有化
    private Singleton(){

    }

    // 提供静态方法
    public static Singleton getInstance(){

        // 懒加载初始化，在第一次使用时才创建实例
        if(instance == null){
            instance = new Singleton();
        }
        return  instance;
    }


    public void display(){
        System.out.println("Hurray! I am create as a Singleton!");
    }


}
