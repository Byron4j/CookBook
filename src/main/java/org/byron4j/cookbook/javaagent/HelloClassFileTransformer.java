package org.byron4j.cookbook.javaagent;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class HelloClassFileTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        System.out.println("加载类:"+className);

        if( "org.byron4j.cookbook.javaagent.Hello".equals(className) ){
            // 通过javassist修改sayHello方法字节码
            try {
                //通过javassist修改sayHello方法字节码

                CtClass ctClass= ClassPool.getDefault().get(className.replace('/','.'));

                CtMethod sayHelloMethod=ctClass.getDeclaredMethod("sayHello");

                sayHelloMethod.insertBefore("System.out.println(\"before sayHello----\");");

                sayHelloMethod.insertAfter("System.out.println(\"after sayHello----\");");

                return ctClass.toBytecode();

            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return classfileBuffer;

    }
}
