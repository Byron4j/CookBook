package org.byron4j.cookbook.designpattern.singleton;

/**
 * 单例模式实例
 * 1. 构造器私有化
 * 2. 提供静态方法供外部获取单例实例
 * 3. 延迟初始化实例
 */
public class SingletonZClone implements  Cloneable{

    private static SingletonZClone instance;

    // 构造器私有化
    private SingletonZClone(){

    }

    // 提供静态方法
    public static SingletonZClone getInstance(){

        // 将同步锁范围缩小，降低性能损耗
        if(instance == null){
            synchronized (SingletonZClone.class){
                if(instance == null){
                    instance = new SingletonZClone();
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
        return super.clone();
    }

    public void display(){
        System.out.println("Hurray! I am create as a SingletonZClone!");
    }


}
