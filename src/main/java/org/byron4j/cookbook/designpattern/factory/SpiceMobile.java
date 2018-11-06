package org.byron4j.cookbook.designpattern.factory;

public abstract  class SpiceMobile {
    public double price;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public abstract void prepare();
    public abstract void bundle();
    public abstract void label();
}
