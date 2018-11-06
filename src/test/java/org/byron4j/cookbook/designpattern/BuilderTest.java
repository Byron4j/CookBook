package org.byron4j.cookbook.designpattern;

import org.byron4j.cookbook.designpattern.builder.Meal;
import org.byron4j.cookbook.designpattern.builder.MealBuilder;
import org.byron4j.cookbook.designpattern.builder.MealDirector;
import org.byron4j.cookbook.designpattern.builder.SandwichBuilder;
import org.junit.Test;

public class BuilderTest {

    @Test
    public void test() {
        /**
         * 创建建造者对象
         */
        MealBuilder mealBuilder = new SandwichBuilder();
        /**
         * 使用建造者对象创建目标对象
         */
        MealDirector.makeMeal(mealBuilder);

        /**
         * 使用建造者对象获取目标对象实例
         */
        Meal meal = mealBuilder.getMeal();

        System.out.println("建造者模式得到的对象实例：" + meal);
    }
}
