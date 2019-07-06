package org.byron4j.cookbook.javaagent;

import javassist.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Javassist1RWBytecode {
    public void sayHello(){
        System.out.println("Hello!");
    }

    public static void main(String[] args){
        // 获取ClassPool
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = null;
        try {
            // 通过ClassPool获取CtClass
            cc = pool.get("org.byron4j.cookbook.javaagent.Rectangle");
            // 设置父类
            cc.setSuperclass(pool.get("org.byron4j.cookbook.javaagent.Point"));
            // 更新到class文件中(仅在JVM中)
            cc.writeFile();

            // 获取修改后的字节码
            byte[] b = cc.toBytecode();
            System.out.println(new String(b));

            // 加载类（请求当前线程的上下文加载器加载CtClass代表的类）
            Class clazz = cc.toClass();
            System.out.println("superClass is :" + clazz.getSuperclass());



            // 定义一个新的类
            ClassPool pool1 = ClassPool.getDefault();
            CtClass cc2 = pool1.makeClass("hello.make.Point");
            System.out.println(cc2.getMethods().length); // 9

            // 追加方法
            cc2.addMethod(CtMethod.make("public void sayHello(){\n" +
                    "        System.out.println(\"Hello!\");\n" +
                    "    }",cc2));
            System.out.println(cc2.getMethods().length);  // 10
            // 被冻结了，不能再修改(Exception in thread "main" java.lang.RuntimeException: hello.make.Point class is frozen)
            // 解冻后可以修改
            cc2.toBytecode();
            cc2.defrost();
            System.out.println(cc2.getFields().length);
            cc2.addField(CtField.make("private String name;", cc2));
            cc2.writeFile();
            System.out.println(cc2.getFields().length);

            // 修剪ctClass
            //cc2.prune();// 设置修剪
            cc2.writeFile();// 冻结的时候，会进行修剪
            cc2.stopPruning(true);
            cc2.debugWriteFile();
            System.out.println(cc2);//修剪后不能访问方法


            //添加class查找路径
            Javassist1RWBytecode hello = new Javassist1RWBytecode();
            pool1.insertClassPath(new ClassClassPath(hello.getClass()));

            // 添加文件目录作为class查找路径
            pool1.insertClassPath("/usr/local/javalib");

            // 添加URL(http://www.javassist.org:80/java/)作为class查找路径，第三个参数必须/开头、第四个参数必须.结尾
            ClassPath cp = new URLClassPath("www.javassist.org", 80, "/java/", "org.javassist.");
            pool1.insertClassPath(cp);


            // byte数组形式class path
            ClassPool pool2 = ClassPool.getDefault();
            byte[] arr = "org.byron4j".getBytes();
            String name = "org.byron4j.Hello";
            pool2.insertClassPath(new ByteArrayClassPath(name, arr));
            CtClass ctClass = pool2.get(name);


            // makeClass
            ClassPool pool3 = ClassPool.getDefault();
            InputStream ins = new FileInputStream("/usr/local/javalib");
            CtClass ctClass1 = pool3.makeClass(ins);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }

    }
}
