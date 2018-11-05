package org.byron4j.cookbook.designpattern.prototype;

public class CakeStore {
    public static void main(String[] args){
        CheeseCake cheeseCake = CheeseCake.builder()
                .sugar("100g")
                .butter("200g")
                .cheese("Acapella")
                .build();
        System.out.println("已存在的实例:" + cheeseCake);

        // 利用原型模式自定义多个蛋糕
        CheeseCake cheeseCake1 = (CheeseCake) cheeseCake.prepareCake();
        cheeseCake1.setName("Byron");
        System.out.println("原型模式的第一个实例:" + cheeseCake1);

        CheeseCake cheeseCake2 = (CheeseCake) cheeseCake.prepareCake();
        cheeseCake2.setName("Joy");
        System.out.println("原型模式的第二个实例:" + cheeseCake2);
    }
}
