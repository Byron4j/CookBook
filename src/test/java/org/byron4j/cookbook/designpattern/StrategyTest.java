package org.byron4j.cookbook.designpattern;

import org.byron4j.cookbook.designpattern.strategy.InsertionSort;
import org.byron4j.cookbook.designpattern.strategy.SelectionSort;
import org.byron4j.cookbook.designpattern.strategy.SortingContext;
import org.junit.Test;

public class StrategyTest {

    @Test
    public void test(){
        int numbers[] = {20, 50, 15, 6, 80};

        SortingContext context = new SortingContext();
        context.setSortingMethod(new InsertionSort());
        context.sortNumbers(numbers);

        System.out.println("***********");
        context.setSortingMethod(new SelectionSort());
        context.sortNumbers(numbers);

    }
}
