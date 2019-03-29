# Class loader

如果必须修改的类是预先知道的，最简单的修改类的方式可能是以下这些：

- 1.通过调用```ClassPool.get()```获取一个```CtClass```对象。
- 2.修改它，并且
- 3.调用CtClass对象的```writeFile()```或者```toBytecode()```方法获得修改后的class文件

如果类是否被修改是在加载时确定的，用户必须让javassist与类加载器协作。
javassist可以与类加载器一起使用，以便在加载时修改字节码。
用户可以使用自定义版本的类加载器，也可以使用javassist提供的类加载器。

## ```CtClass```的```toClass```方法

```CtClass```提供了一个便捷的方法```toClass```，请求当前线程的类加载器去加载CtClass表示的类。调用此方法必须具有适应的权限，否则会抛出一个```SecurityException```异常。

```java
package org.byron4j.cookbook.javaagent;

public class Javassist3ClassLoader {

    public void say(){
        System.out.println("Hello!");
    }
}

```

测试类：
```java
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

```

首先，通过测试类的表示```Javassist3ClassLoader```类的```CtClass```修改其```say```方法，在方法体前面增加一行输出语句；
然后，通过```CtClass```的toClass方法请求当前线程（Javassist3ClassLoaderTest类所在的线程）去加载Javassist3ClassLoader类；
最后，通过Class对象的静态方法newInstance构造一个Javassist3ClassLoader对象，并调用其say方法，得到字节码修改后的方法执行内容结果。

**注意**： 上面的程序依赖于Javassist3ClassLoaderTest类所在的类加载器在调用```toClass```之前没有加载过Javassist3ClassLoader类。

<font color=red>***如果程序运行在web容器中例如JBoss、Tomcat中，***</font> 上下文的类加载器使用```toClass()```方法可能并不适当。在这种情况下，你可能会看到一个不期望的异常```ClassCastException```。为了避免这种情况，你必须明白清楚地给定一个适当的类加载器给```toClass```方法。例如，如果```bean```时你的会话的bean对象：
```java
CtClass cc = ...
Class c = cc.toClass(bean.getClass().getClassLoader());
```


## java中的类加载

在Java，多个类加载器可以共存，每个类加载器创建自己的命名空间。不同的类加载器可以加载具有相同类名的不同class文件，加载的两个类视为不同的类，这一个特性保证我们可以在一个JVM中运行多个应用程序即使这些程序包含相同类名的不同类实力。

>**注意:**
>
>
>
>