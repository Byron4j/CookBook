package org.byron4j.cookbook.designpattern.observer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 观察者2
 */
@AllArgsConstructor
@Data
@Builder
public class Subscriber2 implements  Observer {
    @Override
    public void update(String editon) {
        System.out.println("Subscriber2收到新的版本通知。" + editon);
    }
}
