package org.byron4j.cookbook.designpattern.factory;

public class NYMobileFactory extends  MobileFactory {
    @Override
    public SpiceMobile constructMobile(String model) {
        SpiceMobile spiceMobile = null;

        if( "SpiceFire".equalsIgnoreCase(model) ){
            spiceMobile = new SpicePlus();
            spiceMobile.setPrice(200);
        }else if("SpiceMono".equalsIgnoreCase(model)){
            spiceMobile = new SpiceFire();
            spiceMobile.setPrice(200);
        }

        return spiceMobile;
    }
}
