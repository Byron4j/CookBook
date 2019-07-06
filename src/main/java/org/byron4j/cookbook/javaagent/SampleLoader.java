package org.byron4j.cookbook.javaagent;

import javassist.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class SampleLoader extends ClassLoader {

    private ClassPool pool;

    public SampleLoader() throws NotFoundException {
        pool = new ClassPool();
        pool.insertClassPath("./class"); //下面加载的org.byron4j.cookbook.javaagent.Point类要在此路径下
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        CtClass cc = null;
        try {
            cc = pool.get(name);
            byte[] bytes = cc.toBytecode();
            return defineClass(name, bytes, 0, bytes.length);
        } catch (NotFoundException e) {
            throw new ClassNotFoundException();
        }catch (IOException e) {
            throw new ClassNotFoundException();
        } catch (CannotCompileException e) {
            throw new ClassNotFoundException();
        }

    }

    public static void main(String[] args) throws NotFoundException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, CannotCompileException, IOException {
        SampleLoader s = new SampleLoader();
        Class<?> c = s.loadClass("org.byron4j.cookbook.javaagent.Point");
        c.getDeclaredMethod("main", new Class[]{String[].class}).invoke(null, new Object[]{args});



        // 添加字段给系统类：java.lang.String
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("java.lang.String");
        // 字段
        CtField cf = new CtField(CtClass.intType, "hiddenValue", ctClass);
        cf.setModifiers(Modifier.PUBLIC);
        ctClass.addField(cf);
        ctClass.writeFile();
    }
}
