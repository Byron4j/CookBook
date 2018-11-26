package org.byron4j.cookbook.designpattern.state;

public class GameContext {
    private Player player = new Player();

    public void gameAction(String state) {
        if (state == "healthy") {
            player.attack();
            player.fireBumb();
            player.fireGunblade();
            player.fireLaserPistol();
        } else if (state == "survival") {
            player.survive();
            player.firePistol();
        } else if (state == "dead") {
            player.dead();
        }
    }
}
