package org.byron4j.cookbook.designpattern.factory;

public class LondonMobileFactory extends  MobileFactory {
    @Override
    public SpiceMobile constructMobile(String model) {
        SpiceMobile spiceMobile = null;

        if( "SpiceFire".equalsIgnoreCase(model) ){
            spiceMobile = new SpicePlus();
            spiceMobile.setPrice(300);
        }else if("SpiceBolt".equalsIgnoreCase(model)){
            spiceMobile = new SpiceBolt();
            spiceMobile.setPrice(400);
        }

        return spiceMobile;
    }
}
