package org.byron4j.cookbook.javacore.reflection;

import sun.reflect.Reflection;

/**
 * @program: cookbook
 * @author: Byron
 * @create: 2019/07/19
 */
public class ReflectionTest {


    public static void main(String[] args) {
        Demo demo = new Demo();
        //demo.say();
        new SecurityManager() {
            {
                String name = getClassContext()[1].getSimpleName();
                System.err.println(name == null ? "null" : name);
            }
        };
    }



}

class Demo{
    public void say(){
        // Exception in thread "main" java.lang.InternalError: CallerSensitive annotation expected at frame 1
        //	at sun.reflect.Reflection.getCallerClass(Native Method)
        // 改方法只有jdk中的类才能使用
        System.out.println(Reflection.getCallerClass());
    }
}