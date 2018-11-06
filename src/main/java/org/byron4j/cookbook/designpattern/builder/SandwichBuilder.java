package org.byron4j.cookbook.designpattern.builder;

/**
 * 建造者实现类
 */
public class SandwichBuilder implements MealBuilder{

    /**
     * 依赖组合模式， 该Builder的操作实际是对meal进行的操作
     */
    private Meal meal = new Meal();

    @Override
    public void addSandwich(String sandwich) {
        meal.setSandwich(sandwich);
    }

    @Override
    public void addSides(String sides) {
        meal.setSideOrder(sides);
    }

    @Override
    public void addDrink(String drink) {
        meal.setDrink(drink);
    }

    @Override
    public void addOffer(String offer) {
        meal.setOffer(offer);
    }

    @Override
    public void setPrice(double price) {
        meal.setPrice(price);
    }

    @Override
    public Meal getMeal() {
        return meal;
    }
}
