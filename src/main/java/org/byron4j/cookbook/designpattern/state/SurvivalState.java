package org.byron4j.cookbook.designpattern.state;

public class SurvivalState implements PlayerState {

    @Override
    public void action(Player p) {
        p.survive();
        p.firePistol();
    }
}