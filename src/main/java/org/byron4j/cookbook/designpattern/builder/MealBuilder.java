package org.byron4j.cookbook.designpattern.builder;

/**
 * 建造者接口类
 */
public interface MealBuilder {

    void addSandwich(String sandwich);
    void addSides(String sides);
    void addDrink(String drink);
    void addOffer(String offer);
    void setPrice(double price);
    Meal getMeal();
}
