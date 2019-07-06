package org.byron4j.cookbook.javacore.dynamicproxy.cglibproxy;

import org.mockito.cglib.proxy.Enhancer;
import org.mockito.cglib.proxy.MethodInterceptor;
import org.mockito.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor {

    private Object target;

    public Object getInstance(Object target){
        this.target = target;
        Enhancer enhancer = new Enhancer();
        // 增强器对象将目标对象作为父类
        enhancer.setSuperclass(target.getClass());
        // MethodInterceptor 本身继承了 Callback
        enhancer.setCallback(this);
        // 一定要存在无参构造器
        return enhancer.create();
    }


    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("before invoked...");
        methodProxy.invokeSuper(o, args);
        System.out.println("after invoked.");
        return null;
    }

    public static void main(String[] args){
        CglibProxy cglibProxy = new CglibProxy();
        Object obj = cglibProxy.getInstance(new BookStore());
        System.out.println("cglib代理出来的对象：" + obj);
        BookStore instance = (BookStore)obj;
        instance.buyBook();
    }
}
