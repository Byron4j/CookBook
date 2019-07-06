package org.byron4j.cookbook.javaagent;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.IOException;

public class Javassist2ClassPool {
    public static void main(String[] args) throws NotFoundException, IOException, CannotCompileException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass cc = classPool.get("org.byron4j.cookbook.javaagent.Javassist2ClassPool");

        // 调用该方法后，会将CtClass对象从ClassPool中移除
        cc.detach();




        // 等效于 ClassPool.getDefault();
        ClassPool cp = new ClassPool(true);


        // 级联ClassPool
        ClassPool parent = ClassPool.getDefault();
        ClassPool child = new ClassPool(parent);
        child.insertClassPath("./classes");

        // child classpool在委托之前加载类文件
        ClassPool parent2 = ClassPool.getDefault();
        ClassPool child2 = new ClassPool(parent2);
        child2.appendSystemPath();         // 和默认同样的class查找路径
        child2.childFirstLookup = true;    // 改变child的行为


        // 首先获取表示Point类的一个CtClass对象
        // 然后调用setName修改类名
        ClassPool pool3 = ClassPool.getDefault();
        CtClass cc3 = pool3.get("org.byron4j.cookbook.javaagent.Point");
        cc3.setName("Pair");

        //
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("org.byron4j.cookbook.javaagent.Point");
        CtClass ctClass1 = pool.get("org.byron4j.cookbook.javaagent.Point");
        ctClass.setName("Pair");
        CtClass ctClass2 = pool.get("Pair");
        CtClass ctClass3 = pool.get("org.byron4j.cookbook.javaagent.Point");
        System.out.println(ctClass == ctClass2);  // true;
        System.out.println(ctClass3 == ctClass2); // false;



        // 不同的ClassPool构造表示同一个类的不同的CtClass对象
        ClassPool pool10 = ClassPool.getDefault();
        CtClass ctClass10 = pool10.get("org.byron4j.cookbook.javaagent.Point");
        ClassPool pool20 = new ClassPool(true);
        CtClass ctClass20 = pool20.get("org.byron4j.cookbook.javaagent.Point");
        System.out.println(pool10 == pool20);


        ClassPool pool30 = ClassPool.getDefault();
        CtClass ctClass30 = pool30.get("org.byron4j.cookbook.javaagent.Point");
        ctClass30.writeFile();// 被冻结
        //ctClass30.setName("Pair");// 冻结后不能使用--错误
        pool30.getAndRename("org.byron4j.cookbook.javaagent.Point", "Pair");
    }
}
