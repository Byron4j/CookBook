package org.byron4j.cookbook.javacore.dynamicproxy.jdkproxy;

public interface Hello {

    @DynamicAnno
    public void sayHello();
}
