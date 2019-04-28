package org.byron4j.cookbook.javacore.dynamicproxy.jdkproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JDKDynamicProxy implements InvocationHandler {
    /**
     * 目标对象
     */
    private Hello target;


    JDKDynamicProxy() {
        target = new HelloImpl();
        System.out.println("构造器：" + target);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("=================================================\n查看代理类:" + proxy.getClass().getName());

        Object invoke = null;

        // @DynamicAnno 注解标注在接口上才有效，JDK动态代理是基于接口的，
        if( null != method.getAnnotation(DynamicAnno.class) ){
            System.out.println("before invoked...");
            invoke = method.invoke(target, args);
            System.out.println("after invoked.");
        }else{
            invoke = method.invoke(target, args);
        }


        return  invoke;
    }

    public static void main(String[] args){
        Hello o = (Hello)Proxy.newProxyInstance(JDKDynamicProxy.class.getClassLoader(), new Class[]{Hello.class}, new JDKDynamicProxy());
        o.sayHello();
        System.out.println(o);
    }

    
}
