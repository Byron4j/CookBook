package org.byron4j.cookbook.designpattern.state;

public class Player {
    public void attack(){
        System.out.println("Attack");
    }

    public void fireBumb() {
        System.out.println("Fire Bomb");
    }

    public void fireGunblade() {
        System.out.println("Fire Gunblade");
    }

    public void fireLaserPistol() {
        System.out.println("Laser Pistols");
    }

    public void firePistol() {
        System.out.println("Fire Pistol");
    }

    public void survive() {
        System.out.println("Surviving!");
    }

    public void dead() {
        System.out.println("Dead! Game Over");
    }
}
