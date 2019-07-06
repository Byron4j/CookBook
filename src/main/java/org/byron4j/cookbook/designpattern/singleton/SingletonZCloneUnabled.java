package org.byron4j.cookbook.designpattern.singleton;

/**
 * 单例模式实例
 * 1. 构造器私有化
 * 2. 提供静态方法供外部获取单例实例
 * 3. 延迟初始化实例
 */
public class SingletonZCloneUnabled implements  Cloneable{

    private static SingletonZCloneUnabled instance;

    // 构造器私有化
    private SingletonZCloneUnabled(){

    }

    // 提供静态方法
    public static SingletonZCloneUnabled getInstance(){

        // 将同步锁范围缩小，降低性能损耗
        if(instance == null){
            synchronized (SingletonZCloneUnabled.class){
                if(instance == null){
                    instance = new SingletonZCloneUnabled();
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
        System.out.println("Hurray! I am create as a SingletonZClone!");
    }


}
