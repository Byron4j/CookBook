package org.byron4j.cookbook.javaagent;

import javassist.*;

public class Javassist3ClassLoaderTest {

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get("org.byron4j.cookbook.javaagent.Javassist3ClassLoader");
        CtMethod m = cc.getDeclaredMethod("say");

        m.insertBefore("System.out.println(\"Ready to say:\");");
        Class c = cc.toClass();
        Javassist3ClassLoader javassist3ClassLoader = (Javassist3ClassLoader)c.newInstance();
        javassist3ClassLoader.say();


        Javassist3ClassLoaderTest.class.getClassLoader().loadClass("org.byron4j.cookbook.javaagent.Javassist3ClassLoader");
        // 类加载器展示不同的类
        MyClassLoader myClassLoader = new MyClassLoader();
        Class clazz = myClassLoader.loadClass("org.byron4j.cookbook.javaagent.Javassist3ClassLoader");
        Javassist3ClassLoader j = (Javassist3ClassLoader)clazz.newInstance();
    }
}
