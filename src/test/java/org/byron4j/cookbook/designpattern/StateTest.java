package org.byron4j.cookbook.designpattern;

import org.byron4j.cookbook.designpattern.state.*;
import org.junit.Test;

public class StateTest {
    @Test
    public void test(){
        GameContextUpgrade context = new GameContextUpgrade();

        context.setState(new HealthyState());
        context.gameAction();
        System.out.println("*****");

        context.setState(new SurvivalState());
        context.gameAction();
        System.out.println("*****");

        context.setState(new DeadState());
        context.gameAction();
        System.out.println("*****");
    }
}
