package org.byron4j.cookbook.springMVC.root.controller;

import org.springframework.stereotype.Controller;

@Controller
public class HelloController {
    public void sayHello(){
        System.out.println("Hello!");
    }
}
