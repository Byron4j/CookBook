package org.byron4j.cookbook.springMVC.root;

import org.byron4j.cookbook.springMVC.root.controller.HelloController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@Configuration
@ComponentScan("org.byron4j.cookbook.springMVC")
public class SpringMVCStartup {
    public static void main(String[] args){
        AnnotationConfigApplicationContext annotationConfigApplicationContext
            = new AnnotationConfigApplicationContext();
        HelloController bean = annotationConfigApplicationContext.getBean(HelloController.class);
        bean.sayHello();
    }
}
