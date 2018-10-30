package org.byron4j.cookbook.designpattern.observer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 出版商--发布者
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Publisher implements Subject{

    /**
     * 已注册的观察者列表
     */
    private List<Observer> _observers = new ArrayList<>();



    @Override
    public void registerObserver(Observer observer) {
        System.out.println("注册观察者:" + observer);
        _observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        System.out.println("注销观察者:" + observer);
        _observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : _observers) {
            observer.update("广播消息通知给:" + observer);
        }
    }

    @Override
    public void initObservers() {
        if( null == _observers ){
            _observers = new ArrayList<>();
        }
    }
}
