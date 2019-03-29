package org.byron4j.cookbook.javaagent;

import javassist.*;

public class Javassist3ClassLoaderTest {

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get("org.byron4j.cookbook.javaagent.Javassist3ClassLoader");
        CtMethod m = cc.getDeclaredMethod("say");

        m.insertBefore("System.out.println(\"Ready to say:\");");
        Class c = cc.toClass();
        Javassist3ClassLoader javassist3ClassLoader = (Javassist3ClassLoader)c.newInstance();
        javassist3ClassLoader.say();
    }
}
