package org.byron4j.cookbook.designpattern.observer;

/**
 * 主体
 */
public interface Subject {

    /**
     * 注册主体
     * @param observer
     */
    public void registerObserver(Observer observer);

    /**
     * 移除订阅
     * @param observer
     */
    public void removeObserver(Observer observer);

    /**
     * 移除所有的观察者
     */
    public void notifyObservers();

    /**
     * 初始化主体相关信息
     */
    public void initObservers();
}
