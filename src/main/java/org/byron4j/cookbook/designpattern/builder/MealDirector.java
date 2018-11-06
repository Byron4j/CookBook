package org.byron4j.cookbook.designpattern.builder;

/**
 * 角色扮演： 咖啡餐馆
 */
public class MealDirector {

    public static void makeMeal(MealBuilder mealBuilder){
        mealBuilder.addSandwich("Hamburger");
        mealBuilder.addSides("Fries");
        mealBuilder.addDrink("Coke");
        mealBuilder.addOffer("Weekend Bonanza");
        mealBuilder.setPrice(5.99);
    }
}
