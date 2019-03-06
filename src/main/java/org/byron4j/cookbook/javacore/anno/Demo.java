package org.byron4j.cookbook.javacore.anno;

import java.lang.annotation.Repeatable;


@Repeatable(Role.class)
public @interface Demo {
    String role() default "B";
}


