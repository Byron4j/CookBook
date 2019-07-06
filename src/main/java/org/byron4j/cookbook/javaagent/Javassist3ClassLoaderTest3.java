package org.byron4j.cookbook.javaagent;

import javassist.*;

public class Javassist3ClassLoaderTest3 {

    public static void main(String[] args) throws Throwable {
        Translator t = new MyTranslator();
        ClassPool cp = ClassPool.getDefault();
        Loader loader = new Loader();
        loader.addTranslator(cp, t);
        loader.run("org.byron4j.cookbook.javaagent.Point", args);
    }
}
