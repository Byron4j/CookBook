package org.byron4j.cookbook.designpattern.observer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 观察者1
 */
@AllArgsConstructor
@Data
@Builder
public class Subscriber1 implements  Observer {
    @Override
    public void update(String editon) {
        System.out.println("Subscriber1收到新的版本通知。" + editon);
    }
}
