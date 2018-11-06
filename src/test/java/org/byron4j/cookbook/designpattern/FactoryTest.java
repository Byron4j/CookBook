package org.byron4j.cookbook.designpattern;

import org.byron4j.cookbook.designpattern.factory.LondonMobileFactory;
import org.byron4j.cookbook.designpattern.factory.MobileFactory;
import org.byron4j.cookbook.designpattern.factory.NYMobileFactory;
import org.byron4j.cookbook.designpattern.factory.SpiceMobile;
import org.junit.Test;

public class FactoryTest {

    @Test
    public void test() {
        MobileFactory factory = new LondonMobileFactory();
        SpiceMobile spiceMobile1 = factory.produceMobile("SpiceFire");
        System.out.println(spiceMobile1);

        MobileFactory factory2 = new NYMobileFactory();
        SpiceMobile spiceMobile2 = factory2.produceMobile("SpiceMono");
        System.out.println(spiceMobile2);

    }
}
