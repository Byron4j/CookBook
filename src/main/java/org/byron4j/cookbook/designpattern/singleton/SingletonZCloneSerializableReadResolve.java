package org.byron4j.cookbook.designpattern.singleton;

import java.io.Serializable;

/**
 * 单例模式实例
 * 1. 构造器私有化
 * 2. 提供静态方法供外部获取单例实例
 * 3. 延迟初始化实例
 */
public class SingletonZCloneSerializableReadResolve implements  Cloneable, Serializable {

    private static SingletonZCloneSerializableReadResolve instance;

    // 构造器私有化
    private SingletonZCloneSerializableReadResolve(){

    }

    // 提供静态方法
    public static SingletonZCloneSerializableReadResolve getInstance(){

        // 将同步锁范围缩小，降低性能损耗
        if(instance == null){
            synchronized (SingletonZCloneSerializableReadResolve.class){
                if(instance == null){
                    instance = new SingletonZCloneSerializableReadResolve();
                }
            }
        }
        return  instance;
    }

    /**
     * 克隆方法--改为public
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }



    public void display(){
        System.out.println("Hurray! I am create as a SingletonZCloneSerializableReadResolve!");
    }

    /**
     * 反序列化时返回instance实例，防止破坏单例模式
     * @return
     */
    protected Object readResolve(){
        return getInstance();
    }
}
