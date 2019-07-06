package org.byron4j.cookbook.javaagent;

import javassist.*;

public class MyTranslator implements Translator {
    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
        System.out.println("org.byron4j.cookbook.javaagent.MyTranslator.start invoked!" );
    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        // 在类加载前执行该方法，所以可以改变类的定义
        CtClass cc = pool.get(classname);
        cc.setModifiers(Modifier.PUBLIC);
    }
}
