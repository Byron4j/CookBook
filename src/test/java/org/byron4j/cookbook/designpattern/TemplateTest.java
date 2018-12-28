package org.byron4j.cookbook.designpattern;

import org.byron4j.cookbook.designpattern.template.MexicanPizza;
import org.byron4j.cookbook.designpattern.template.SweetCornPizza;
import org.junit.Test;

public class TemplateTest {

    @Test
    public void test(){
        SweetCornPizza pizza1 = new SweetCornPizza();
        pizza1.preparePizza();

        System.out.println("**********");

        MexicanPizza pizza2 = new MexicanPizza();
        pizza2.preparePizza();
    }
}
