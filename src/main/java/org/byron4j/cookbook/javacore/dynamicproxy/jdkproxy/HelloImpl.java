package org.byron4j.cookbook.javacore.dynamicproxy.jdkproxy;

public class HelloImpl implements Hello {

    @Override
    public void sayHello() {
        System.out.println("目标方法:Hello.");
    }
}
