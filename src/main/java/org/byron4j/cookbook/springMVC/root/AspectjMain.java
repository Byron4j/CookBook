package org.byron4j.cookbook.springMVC.root;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 定义切面
 */
@Component
@Aspect
public class AspectjMain {

    // 切点（连接点集合）
    @Pointcut("execution(* org.byron4j.cookbook.springMVC.root.controller.*.*(..))")
    public void pointMethod(){
        //
    }

    /*
    通知
     */
    @Before("org.byron4j.cookbook.springMVC.root.AspectjMain.pointMethod()")
    public void doAccessCheck() {
        System.out.println("Before---doAccessCheck()");
    }
}
