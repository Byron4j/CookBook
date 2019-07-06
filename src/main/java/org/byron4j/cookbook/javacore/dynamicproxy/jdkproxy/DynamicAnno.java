package org.byron4j.cookbook.javacore.dynamicproxy.jdkproxy;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DynamicAnno {
}
