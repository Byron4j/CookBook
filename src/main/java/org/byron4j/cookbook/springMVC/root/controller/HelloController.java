package org.byron4j.cookbook.springMVC.root.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;

@Controller
public class HelloController {
    public void sayHello(){
        System.out.println("Hello!");
        StopWatch clock = new StopWatch(getClass().getName());
        clock.start("org.byron4j.cookbook.springMVC.root.controller.HelloController#sayHello");
        try {
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            clock.stop();
            System.out.println(clock.prettyPrint());
        }
    }
}
