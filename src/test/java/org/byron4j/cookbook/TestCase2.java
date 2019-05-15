package org.byron4j.cookbook;

import lombok.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TestCase2 {

    public static void main(String[] args){
        List<Dish> dishList = new ArrayList<>();
        Dish dish1 = new Dish("001", "张三");
        dishList.add(dish1);
        Dish dish2 = new Dish("001", "李四");
        dishList.add(dish2);
        Dish dish3 = new Dish("002", "王五");
        dishList.add(dish3);

        System.out.println(dishList);


        TreeSet<Dish> treeSet = dishList.stream()
                .collect(Collectors.toCollection(() -> new TreeSet<Dish>(Comparator.comparing(Dish::getId))));
        System.out.println(treeSet);
    }



    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    static  class Dish {

        private String id;

        private String name;

    }
}
