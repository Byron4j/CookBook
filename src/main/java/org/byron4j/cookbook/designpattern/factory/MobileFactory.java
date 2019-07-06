package org.byron4j.cookbook.designpattern.factory;

public abstract  class MobileFactory {
    public abstract SpiceMobile constructMobile(String model);

    /**
     * 这可以算是模板方法了...^_*
     * @param model
     * @return
     */
    public SpiceMobile produceMobile(String model){
        SpiceMobile spiceMobile = constructMobile(model);
        spiceMobile.prepare();
        spiceMobile.bundle();
        spiceMobile.label();
        return spiceMobile;
    }
}
