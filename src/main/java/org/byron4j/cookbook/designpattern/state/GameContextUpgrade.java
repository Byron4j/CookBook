package org.byron4j.cookbook.designpattern.state;

public class GameContextUpgrade {
    private PlayerState state = null;
    private Player player = new Player();

    public void setState(PlayerState state) {
        this.state = state;
    }

    public void gameAction() {
        state.action(player);
    }
}
