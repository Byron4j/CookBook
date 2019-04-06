package org.byron4j.cookbook.javaagent;

import javassist.*;

public class Javassist3ClassLoaderTest2 {

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        ClassPool pool = ClassPool.getDefault();
        // 使用ClassPool创建Loader
        Loader cl = new Loader(pool);

        CtClass ct = pool.get("org.byron4j.cookbook.javaagent.Rectangle");
        ct.setSuperclass(pool.get("org.byron4j.cookbook.javaagent.Point"));

        Class<?> c = cl.loadClass("org.byron4j.cookbook.javaagent.Rectangle");
        Object o = c.newInstance();
    }
}
