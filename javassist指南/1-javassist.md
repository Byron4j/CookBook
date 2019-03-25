# javassist编程指南（主译）

## javassist是什么？

- Javassist(Java 编程辅助)使得Java字节码操作更简单。
- Javassist可用于编辑字节码的类库。
- 允许Java程序可以在运行时定义一个新的class、在JVM加载时修改class文件。

  不像其它的字节码编辑器，javassist提供了2各层次的API：源码级别、字节码级别。如果用户使用了源码级别的API，就可以在不了解Java字节码规范的情况下编辑class文件。整个API是基于Java语言词汇设计的。你甚至可以以源码文本形式指定插入字节码，javassist编译它是非常快的。另一方面。字节码层次的API允许用户像其它编辑器一样直接编辑class文件。
  
## 引入javassist的Maven依赖

```xml
<dependency>
    <groupId>org.javassist</groupId>
    <artifactId>javassist</artifactId>
    <version>3.22.0-GA</version>
</dependency>
```

## 使用javassist进行编程

 [javassist官网](http://www.javassist.org/)：[官方指南](http://www.javassist.org/tutorial/tutorial.html)
 
- 1.[读、写字节码]() 
- 2.[ClassPool]() 
- 3.[Class loader]() 
- 4.[Introspection and customization]() 
- 5.[Bytecode level API]() 
- 6.[Generics]() 
- 7.[Varargs]() 
- 8.[J2ME]() 
- 9.[Boxing/Unboxing]() 
- 10.[Debug]()


### 读、写字节码

  Javassist是一个处理字节码的类库。Java字节码存储在一个叫做*.class的二进制文件中。每个class文件包含一个java类或者接口。
  
  ```javassist.CtClass``` 代表一个class文件的抽象类表示形式。一个```CtClass```(compile-time class编译时的类)是一个处理class文件的句柄，以下是一个简单的程序：

```java
ClassPool pool = ClassPool.getDefault();
CtClass cc = pool.get("test.Rectangle");
cc.setSuperclass(pool.get("test.Point"));
cc.writeFile();
```

这段程序首先包含一个```ClassPool```对象，通过javassist控制字节码的修改。```ClassPool```对象是代表class文件的```CtClass```对象的容器。它根据构造一个```CtClass```对象的需求读取一个class文件，并记录被构建好的对象以供将来进行访问。
为了修改一个类的定义，用户必须首先从```ClassPool```对象的```.get(className)```方法获取一个```CtClass```引用。
在上述示例中，```CtClass```对象表示```ClassPool```中的类```test.Rectangle```，并且将其分配给变量```cc```。
```ClassPool```对象由静态方法```getDefault```方法查找默认的系统检索path返回。

>从实现上来看，```ClassPool```是一个```CtClass```的哈希表，使用class name作为key。
>
>```ClassPool.get()```方法通过检索这个哈希表找到一个```CtClass```对象关联指定的key。
>
>如果```CtClass```对象没有找到，```get()```方法会读取class文件去构造一个```CtClass```对象，记录在哈希表中然后作为```get()```的返回值返回。

从```ClassPool```中获取到的```CtClass```对象是可以被修改的。在上述示例中，它被修改了， ```test.Rectangle```的父类变更为```test.Point```,这个修改将会在最后```CtClass.writeFile()```方法调用后反映在class文件中。

```writeFile()``` 方法将```CtClass```对象转换到class文件并且将其写入本地磁盘。Javassist也提供了一个方法用于直接获取修改后的字节码：```toBytecode()```:
```java
byte[] b = cc.toBytecode();
```  

也可以像这样直接加载```CtClass```:
```java
Class clazz = cc.toClass();
```

```toClass``` 请求当前线程的上下文类加载器去加载class文件，返回一个```java.lang.Class```对象。

示例：

```java
package org.byron4j.cookbook.javaagent;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.IOException;

public class Hello {
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
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }

    }
}

```

#### 定义一个新的class

重新定义一个新的类，```ClassPool.makeClass```方法将会被调用：

```java
// 定义一个新的类
ClassPool pool1 = ClassPool.getDefault();
CtClass cc2 = pool1.makeClass("hello.make.Point");
System.out.println(cc2.toClass()); // 输出class hello.make.Point
```

这个程序定义了一个```Point```类，未包含任何成员，成员方法可以通过使用```CtClass```的```addMethod()```方法传入一个```CtMethod```的工厂方法创建的对象作为参数来追加。

```java
// 定义一个新的类
ClassPool pool1 = ClassPool.getDefault();
CtClass cc2 = pool1.makeClass("hello.make.Point");
//System.out.println(cc2.toClass().getMethods().length); // 9

// 追加方法
cc2.addMethod(CtMethod.make("public void sayHello(){\n" +
        "        System.out.println(\"Hello!\");\n" +
        "    }",cc2));
System.out.println(cc2.toClass().getMethods().length);  // 10
```


```makeClass()```方法不能创建一个新的接口，需要使用```makeInterface()```方法才可以。
接口中的成员方法可以通过```CtMethod```的```abstractMethod```方法创建。

#### 冻结类Frozen class

>**冻结类的含义**
>
>如果一个```CtClass```对象通过```writeFile()```、```doBytecode```、```toClass```方法被转换到class文件中，javassist则会冻结这个```CtClass```对象。再对这个```CtClass```对象进行操作则会不允许，这在开发者他们尝试去修改一个已经被JVM加载过的class文件的时候会发出警告，因为JVM不允许重加载一个class。

一个冻结的```CtClass```可以通过其```defrost()```方法解冻，解冻后可以允许对这个CtClass修改：
```java
// 被冻结了，不能再修改(Exception in thread "main" java.lang.RuntimeException: hello.make.Point class is frozen)
// 解冻后可以修改
cc2.toBytecode();// 被冻结
cc2.defrost();// 解冻
System.out.println(cc2.getFields().length);
cc2.addField(CtField.make("private String name;", cc2));// 解冻后允许修改
cc2.writeFile();
System.out.println(cc2.getFields().length);

```

#### 修剪prune类

如果```CtClass.prune()```方法被调用，则Javassist会在CtClass被冻结的时候(调用```writeFile()```、```doBytecode```、```toClass```方法的时候)会修剪CtClass对象的数据结构。
为了降低内存消耗，修剪时会放弃对象中的不必要的属性。当一个CtClass对象被修剪后，方法的字节码则不能被访问除了方法名称、方法签名和注解。修剪过的CtClass对象不会被解冻。默认修剪值是false。


```java
// 修剪ctClass
cc2.prune();// 设置修剪伪true
cc2.writeFile();// 冻结的时候，会进行修剪
System.out.println(cc2);//修剪后不能访问方法
```

禁止修剪```stopPruning(true)```，必须在对象的前面调用：

```java
CtClasss cc = ...;
cc.stopPruning(true);// 前面调用禁止修剪
    :
cc.writeFile();            
```

>**注意：**
>
>当debugging的时候，你可能想临时禁止修剪、冻结和修改一个class文件到磁盘中，那么```debugWriteFile```是一个简便的方法。该方法禁止修剪、写入class文件、解冻。
>




























