package org.byron4j.cookbook.designpattern.segregation;

public class XeroxMachine implements  IMachine {
    @Override
    public void print() {
        System.out.println("打印任务...");
    }

    @Override
    public void staple() {
        System.out.println("装订任务...");
    }

    @Override
    public void scan() {
        System.out.println("扫描任务...");
    }

    @Override
    public void photoCopy() {
        System.out.println("复印任务...");
    }
}
