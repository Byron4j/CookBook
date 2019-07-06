package org.byron4j.cookbook.designpattern.template;

public class MexicanPizza extends Pizza {
    @Override
    public void chooseBread() {
        System.out.println("Choosing Mexican Pizza Bread!");
    }

    @Override
    public void addIngredients() {
        System.out.println("Adding Mexican Pizza Ingredients!");
    }
}