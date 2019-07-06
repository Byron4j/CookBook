package org.byron4j.cookbook.designpattern.observer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 出版商--发布者---弱引用观察者列表--防止内存泄漏
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Publisher2WeakReference implements Subject{

    /**
     * 已注册的观察者列表
     */
    private List<WeakReference<Observer>> _observers = new ArrayList<>();



    @Override
    public void registerObserver(Observer observer) {
        System.out.println("注册观察者:" + observer);
        _observers.add(new WeakReference<Observer>(observer));
    }

    @Override
    public void removeObserver(Observer observer) {
        System.out.println("注销观察者:" + observer);
        _observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (WeakReference<Observer> observer : _observers) {
            observer.get().update("广播消息通知给:" + observer.get());
        }
    }

    @Override
    public void initObservers() {
        if( null == _observers ){
            _observers = new ArrayList<>();
        }
    }
}
