package org.byron4j.cookbook.springMVC.root;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 定义切面
 */
@Aspect
public class AspectjMain {

    // 切点（连接点集合）
    @Pointcut("execution(public org.byron4j.cookbook.springMVC.root.controller.* *(..))")
    public void pointMethod(){
        //
    }

    /*
    通知
     */
    @Before("execution(* org.byron4j.cookbook.springMVC.root.controller.*.*(..))")
    public void doAccessCheck() {
        System.out.println("Before---doAccessCheck()");
    }
}
