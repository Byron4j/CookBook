package org.byron4j.cookbook.designpattern.state;

public class HealthyState implements PlayerState {

    @Override
    public void action(Player p) {
        p.attack();
        p.fireBumb();
        p.fireGunblade();
        p.fireLaserPistol();
    }
}