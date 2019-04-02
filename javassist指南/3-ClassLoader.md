# Class loader 类加载

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

***如果程序运行在web容器中例如JBoss、Tomcat中，*** 上下文的类加载器使用```toClass()```方法可能并不适当。在这种情况下，你可能会看到一个不期望的异常```ClassCastException```。为了避免这种情况，你必须明白清楚地给定一个适当的类加载器给```toClass```方法。例如，如果```bean```是你的会话的bean对象：
```java
CtClass cc = ...
Class c = cc.toClass(bean.getClass().getClassLoader());
```


## java中的类加载

在Java，多个类加载器可以共存，每个类加载器创建自己的命名空间。不同的类加载器可以加载具有相同类名的不同class文件，加载的两个类视为不同的类，这一个特性保证我们可以在一个JVM中运行多个应用程序即使这些程序包含相同类名的不同类实例。

>**注意:**
>
>JVM不允许动态的重新加载一个类。一旦一个类加载器加载了一个类后，它就不能在运行时再重新加载一个新的版本的类了。
>
>因此，你不能在JVM加载类后，再去变更类的定义。
>
>但是，JPDA（Java平台调试架构）提供了有限的类重加载能力。

如果相同的class文件被不同的类加载器加载了，JVM会使用相同的名称和定义创建两个不同的类，这两个类会被看做是不同的。既然这两个类是不同的，所以一个类的实例就不能分配给另一个类类型的变量了。

```java
MyClassLoader myLoader = new MyClassLoader();
Class clazz = myLoader.loadClass("Box");
Object obj = clazz.newInstance();
Box b = (Box)obj;
```

>**多个类加载器形成一个树结构**:
>
>每个类加载器（引导加载器BootstrapClassLoader除外）都有一个父的类加载器（通常是加载了该子类加载器的类）。因为请求去加载一个类可以沿着这个类加载器层级委托，一个类可以被不是你请求的类加载器去加载。因此，被请求去加载一个类C的类加载器和实际加载这个类C的加载器可能不是同一个类加载器。以示区别，我们将前面的加载器称为<font color=red>***C的启动器***</font>，后面的称为<font color=red>***C的真实加载器***</font>。
>
>此外，如果一个类加载器CL被请求去加载一个类C（C的启动器）委托给了它的父类加载器PL，之后，类加载器CL则再也不会被请求去加载类C定义中引用的任何类。
>
>CL不是类C的引用的类的启动器，相反，PL成为了类C的引用的类的启动器，且PL将会被请求去加载它们。***类C的定义的引用的类将会被类C的真实加载器去加载。***

为了解释这个行为，我们思考下以下示例：

```java
public class Point {    // 被父类加载器PL加载
    private int x, y;
    public int getX() { return x; }
        :
}

public class Box {      // 引导器是CL，但是真实加载器是PL
    private Point upperLeft, size;
    public int getBaseX() { return upperLeft.x; }
        :
}

public class Window {    // 被类加载器CL加载
    private Box box;
    public int getBaseX() { return box.getBaseX(); }
}
```

假设一个类```Window```被一个类加载器CL加载了，则它的引导器和真实加载器都是CL。因为类```Window```的定义引用了类```Box```，JVM将会请求CL去加载```Box```。在这里，假设CL将这个任务委托给父加载器PL。```Box```类的引导器是CL但是真实加载器是PL。在这个案例中，```Point```类的引导器不是CL而是PL，因为它与```Box```的真实加载器相同。CL再也不会被请求去加载```Point```。

再看一个有些细微差异的示例：

```java
public class Point {
    private int x, y;
    public int getX() { return x; }
        :
}

public class Box {      // 引导器是CL，但是真实加载器是PL
    private Point upperLeft, size;
    public Point getSize() { return size; }
        :
}

public class Window {    // 被CL加载
    private Box box;
    public boolean widthIs(int w) {
        Point p = box.getSize();
        return w == p.getX();
    }
}
```

现在```Window```类的定义也引用了```Point```类，在这个案例中，CL在被请求加载```Point```时也将委托给PL。***你必须避免存在两个不同的类加载器重复加载同一个类。***，二者中的其中一个必须委托给另外一个。
如果在```Point```加载的时候，CL没有委托给PL，```widthIs()```将会抛出一个```ClassCastException```。因为```Box```的真实加载器是PL，```Box```中引用的类```Point```类也会被PL加载。因此，```getSize()```方法返回值是PL加载的```Point```的一个实例，然而```getSize()```方法中的变量是CL加载的```Point```类型，JVM将它们视作不同的类型，所以会抛出类型不匹配的异常。

这种行为有些不方便但是是可行的,如果以下语句：
```java
Point p = box.getSize();
```
不会抛出一个异常，则```Window```的程序猿就打破了```Point```类的封装性。比如，```Point```中的所有属性```x```是PL加载的。然而，```Window```类可以直接访问```x```的值，如果CL通过以下定义加载```Point```的话：

```java
public class Point {
    public int x, y;    // 非私有属性
    public int getX() { return x; }
        :
}
```

如果要获取更多关于Java中的类加载问题，以下论文可能是有帮助的：
```
    Sheng Liang and Gilad Bracha, "Dynamic Class Loading in the Java Virtual Machine", 
    ACM OOPSLA'98, pp.36-44, 1998.
```

## 使用```javassist.Loader```

Javassist提供了一个类加载器```javassist.Loader```，这个类加载器使用```javassist.ClassPool```对象读取class文件。

例如，```javassist.Loader```可用于使用javassist修改的指定的类：

```java
ClassPool pool = ClassPool.getDefault();
// 使用ClassPool创建Loader
Loader cl = new Loader(pool);

CtClass ct = pool.get("org.byron4j.cookbook.javaagent.Rectangle");
ct.setSuperclass(pool.get("org.byron4j.cookbook.javaagent.Point"));

Class<?> c = cl.loadClass("org.byron4j.cookbook.javaagent.Rectangle");
Object o = c.newInstance();
```
这个程序修改了类Rectangle类，将其父类设置为Point类，然后程序加载了修改后的Rectangle类，并且创建了一个实例。

**如果用户想在加载一个类的时候按需修改它，则用户可以添加一个```javassist.Loader```的事件监听器。当这个类加载器加载一个类的时候就会通知添加好的事件监听器。** 事件监听器必须实现以下接口：
```java

/**
* Loader的观察者
*/
public interface Translator {
    /**
    * 当对象附加到加载器对象时，加载器将调用该对象进行初始化。此方法可用于获取(用于缓存)一些将在Translator的onLoad()中访问的CtClass对象。
    * @param pool
    * @throws NotFoundException
    * @throws CannotCompileException
    */
    void start(ClassPool pool)
            throws NotFoundException, CannotCompileException;
    
    /**
    * 当Loader加载一个类后，就会通知调用该方法。Loader会在<code>onLoad()</code>方法返回后调用
    * <pre>
    *     pool.get(classname).toBytecode()
    * </pre>
    * 方法去读取class文件，classname可能是尚未创建的类的名称。
    * 如果这样的话，<code>onLoad()</code>方法必须创建那个class，以便Loader可以在<code>onLoad()</code>方法返回后读取它。
    * @param pool
    * @param classname
    * @throws NotFoundException
    * @throws CannotCompileException
    */
    void onLoad(ClassPool pool, String classname)
            throws NotFoundException, CannotCompileException;
}
```

当```javassist.Loader```对象的```addTranslator()```方法添加事件监听器的时候，```start()```方法就会被调用。
```onLoad()```方法会在```javassist.Loader```加载一个类之前被调用。
以下是这两种情况的源码：
```java
// 添加事件监听器的时候，就会调用监听器的start方法
public void addTranslator(ClassPool cp, Translator t)
    throws NotFoundException, CannotCompileException {
    source = cp;
    translator = t;
    t.start(cp);
}

// 存在监听器，则在Loader的findClass方法中，先执行监听器的onLoad()方法，再通过.get(name).toBytecode()加载类
if (source != null) {
    if (translator != null)
        translator.onLoad(source, name);

    try {
        classfile = source.get(name).toBytecode();
    }
    catch (NotFoundException e) {
        return null;
    }
}
```

**所以，```translator.onLoad```的方法中可以修改加载的类的定义。**

以下示例，事件监听器在将所有的类改为public修饰：
```java
public class MyTranslator implements Translator {
    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        // 在类加载前执行该方法，所以可以改变类的定义
        CtClass cc = pool.get(classname);
        cc.setModifiers(Modifier.PUBLIC);
    }
}
```

注意```onLoad()```方法没有去调用```toBytecode()```或者```writeFile()```，因为```javassist.Loader```会调用这些方法来获取class文件。

要使用```MyTranslator```来运行一个应用程序，main类可以如下编写：
```java
public class Point {
    public static void main(String[] args){
        System.out.println("org.byron4j.cookbook.javaagent.Point#main invoked!");
    }
}

public static void main(String[] args) throws Throwable {
    Translator t = new MyTranslator();
    ClassPool cp = ClassPool.getDefault();
    Loader loader = new Loader();
    loader.addTranslator(cp, t);
    // loader.run方法会运行指定classname的main方法
    loader.run("org.byron4j.cookbook.javaagent.Point", args);
}
```
运行输出： ```org.byron4j.cookbook.javaagent.Point#main invoked!```

注意：应用的类像Point是不能访问加载器的类如MyTranslator、ClassPool的，因为它们是被不同的加载器加载的。应用的类是由javassist.Loader加载，而其他的是由默认的JVM类加载器加载的。

```javassist.Loader```以和```java.lang.ClassLoader```不同的顺序加载类。
```ClassLoader```首先将加载操作委托给父加载器，如果父加载器找不到它们才由自身尝试加载类。
反过来说，```javassist.Loader```在委托给父加载器之前尝试加载类。只有当：
- 类不是由```ClassPool.get()```找到的，或者
- 类使用了```delegateLoadingOf()```去指定由父加载器加载。

这个搜索顺序允许Javassist加载修改过的类。然而，如果加载失败的话就会委托给父加载器去加载。一旦一个类由其父加载器加载了，这个类引用的其它类也会由其父加载器加载，则这些类不会被修改了。