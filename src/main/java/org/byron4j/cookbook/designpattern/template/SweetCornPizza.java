package org.byron4j.cookbook.designpattern.template;

public class SweetCornPizza extends Pizza {
    @Override
    public void chooseBread() {
        System.out.println("Choosing SweetCorn Pizza Bread!");
    }

    @Override
    public void addIngredients() {
        System.out.println("Adding SweetCorn Pizza Ingredients!");
    }
}
