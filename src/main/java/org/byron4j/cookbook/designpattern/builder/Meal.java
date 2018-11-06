package org.byron4j.cookbook.designpattern.builder;

/**
 * 晚餐实体类
 */
public class Meal {

    private String sandwich;
    private String sideOrder;
    private String drink;
    private String offer;
    private double price;

    @Override
    public String toString() {
        return "Meal{" +
                "sandwich='" + sandwich + '\'' +
                ", sideOrder='" + sideOrder + '\'' +
                ", drink='" + drink + '\'' +
                ", offer='" + offer + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    public void setSandwich(String sandwich) {
        this.sandwich = sandwich;
    }

    public void setSideOrder(String sideOrder) {
        this.sideOrder = sideOrder;
    }

    public void setDrink(String drink) {
        this.drink = drink;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
