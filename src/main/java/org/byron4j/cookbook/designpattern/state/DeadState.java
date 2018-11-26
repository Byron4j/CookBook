package org.byron4j.cookbook.designpattern.state;

public class DeadState implements PlayerState {

    @Override
    public void action(Player p) {
        p.dead();
    }
}