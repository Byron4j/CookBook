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

