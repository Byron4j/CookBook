# JAVA获取CLASSPATH路径

```java
ClassLoader 提供了两个方法用于从装载的类路径中取得资源：

        public URL  getResource (String name);  
        public InputStream  getResourceAsStream (String name);  

这里name是资源的类路径，它是相对与“/”根路径下的位置。getResource得到的是一个URL对象来定位资源，
而getResourceAsStream取得该资源输入流的引用保证程序可以从正确的位置抽取数据。
但是真正使用的不是ClassLoader的这两个方法，而是Class的 getResource和getResourceAsStream方法，
因为Class对象可以从你的类得到（如YourClass.class或 YourClass.getClass()），
而ClassLoader则需要再调用一次YourClass.getClassLoader()方法，不过根据JDK文档的说法，
Class对象的这两个方法其实是“委托”（delegate）给装载它的ClassLoader来做的，所以只需要使用 Class对象的这两个方法就可以了。

       因此，直接调用  this.getClass().getResourceAsStream(String name) ;获取流，
                静态化方法中则使用ClassLoader.getSystemResourceAsStream (String name) ; 。

      下面是一些得到classpath和当前类的绝对路径的一些方法。你可能需要使用其中的一些方法来得到你需要的资源的绝对路径。

1.this.getClass().getResource（""） 
得到的是当前类class文件的URI目录。不包括自己！
如：file：/D：/workspace/jbpmtest3/bin/com/test/

2.this.getClass().getResource（"/"） 
得到的是当前的classpath的绝对URI路径 。
如：file：/D：/workspace/jbpmtest3/bin/

3.this.getClass() .getClassLoader().getResource（""） 
得到的也是当前ClassPath的绝对URI路径 。
如：file：/D：/workspace/jbpmtest3/bin/

4.ClassLoader.getSystemResource（""） 
得到的也是当前ClassPath的绝对URI路径 。
如：file：/D：/workspace/jbpmtest3/bin/

5.Thread.currentThread().getContextClassLoader ().getResource（""） 
得到的也是当前ClassPath的绝对URI路径 。
如：file：/D：/workspace/jbpmtest3/bin/

6.ServletActionContext.getServletContext().getRealPath(“/”) 
Web应用程序 中，得到Web应用程序的根目录的绝对路径。这样，我们只需要提供相对于Web应用程序根目录的路径，就可以构建出定位资源的绝对路径。
如：file：/D:/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/WebProject


注意点：

1.尽量不要使用相对于System.getProperty（"user.dir"）当前用户目录的相对路径。这是一颗定时炸弹，随时可能要你的命。

2.尽量使用URI形式的绝对路径资源。它可以很容易的转变为URI，URL，File对象。

3.尽量使用相对classpath的相对路径。不要使用绝对路径。
使用上面ClassLoaderUtil类的public static URL getExtendResource（String relativePath）方法已经能够使用相对于classpath的相对路径定位所有位置的资源。

4.绝对不要使用硬编码的绝对路径。因为，我们完全可以使用ClassLoader类的getResource（""）方法得到当前classpath的绝对路径。
如果你一定要指定一个绝对路径，那么使用配置文件，也比硬编码要好得多！

获得CLASSPATH之外路径的方法：
URL base = this.getClass（）.getResource（""）； //先获得本类的所在位置，如/home/popeye/testjava/build/classes/net/  
      String path = new File（base.getFile（）， "……/……/……/"+name）.getCanonicalPath（）； //就可以得到/home/popeye/testjava/name

另外，如果从ANT启动程序，this.getClass().getResource("")取出来的比较怪，直接用JAVA命令行调试就可成功。
```