@[toc]
# Java核心（三）反射
Java反射给我们提供了在运行时检查甚至修改应用行为的机制。 反射是java高级的核心技术，所有有经验的程序员都应该理解。

通过反射机制，我们可以在运行时检视 类、接口、枚举，获得他们的结构、方法以及属性信息，即使在编译期类是不可访问的。   我们也可以通过反射创建类实例，调用它的方法，或者改变属性值。

## Java中的反射

Java的反射是一种很强大的机制，在正常的编程中使用并不多，但它是java的主干，很多Java EE 框架均使用了反射技术：

-  **JUnit**  利用反射技术解析@Test注解，从而得到测试的方法并调用它们。

-  **Spring**  依赖注入是java反射的典型应用

-  **Tomcat**  web容器通过解析web.xml文件和请求url，将请求正确的转发到对应的模块。

-  **Eclipse**  自动完成方法的名称输入

-  **Struts**

-  **Hibernate**

以上这个清单只是小部分，它们全部使用了反射技术，因为正常情况下，它们无法访问用户编写的类、接口以及方法等。


但是我们不建议在正常编程中滥用反射技术，因为我们拥有自己编写的类的访问权限了，反射存在以下几个缺陷：

-  **性能较差**   尽管反射解决了动态类型的问题，但是也引入了在classpath 扫描类进行加载的过程，会影响性能。

-  **安全限制**  反射需要在运行时获得访问权限，但是在security manager中可能是不允许的。  这可能会导致应用运行失败。

-  **安全问题**  通过反射我们可以访问那些不建议我们访问的类，例如我们可以访问private的属性并修改其值。  这可能引发安全问题导致应用异常。

-  **较高的维护代价**  反射相关的代码难以理解以及调试，代码的错误不能在编译期展现出来，使用反射的代码灵活性不高并难以维护。

## 反射在类中的使用

在java中，任何对象要么是原始类型或者引用类型。 所有的类、枚举、数据和其他引用类型均继承自Object类。

<font color=red size=5><b>java.lang.Class</font>是所有反射操作的入口。对于任何类型的对象，JVM 会初始化其一个不可变的java.lang.Class 实例来提供检查对象的运行时的属性、创建新对象、调用方法、get/set 属性。

我们来看看Class的重要方法，为了方便起见，我们先创建一些类和接口。

```java
package com.byron4j.hightLevel.reflection;

public interface BaseInterface {
	
	public int interfaceInt=0;
	
	void method1();
	
	int method2(String str);
}

```

```java
package com.byron4j.hightLevel.reflection;

public class BaseClass {
	
public int baseInt;
	
	private static void method3(){
		System.out.println("Method3");
	}
	
	public int method4(){
		System.out.println("Method4");
		return 0;
	}
	
	public static int method5(){
		System.out.println("Method5");
		return 0;
	}
	
	void method6(){
		System.out.println("Method6");
	}
	
	// piblic 的内部类
	public class BaseClassInnerClass{}
		
	// public 的枚举
	public enum BaseClassMemberEnum{}
	
}

```

```java
package com.byron4j.hightLevel.reflection;

public class ConcreteClass extends BaseClass implements BaseInterface{

	public int publicInt;
	private String privateString="private string";
	protected boolean protectedBoolean;
	Object defaultObject;
	
	public ConcreteClass(int i){
		this.publicInt=i;
	}

	@Override
	public void method1() {
		System.out.println("Method1 impl.");
	}

	@Override
	public int method2(String str) {
		System.out.println("Method2 impl.");
		return 0;
	}
	
	@Override
	public int method4(){
		System.out.println("Method4 overriden.");
		return 0;
	}
	
	public int method5(int i){
		System.out.println("Method4 overriden.");
		return 0;
	}
	
	// inner classes
	public class ConcreteClassPublicClass{}
	private class ConcreteClassPrivateClass{}
	protected class ConcreteClassProtectedClass{}
	class ConcreteClassDefaultClass{}
	
	//member enum
	enum ConcreteClassDefaultEnum{}
	public enum ConcreteClassPublicEnum{}
	
	//member interface
	public interface ConcreteClassPublicInterface{}

	
}

```


下面来看看使用反射的常用方法。

#### 获得Class对象

我们可以通过三种方式获取对象的Class实例：

-  通过静态变量class

-  使用示例的getClass()方法

-  <font color=red size=5><b>java.lang.Class.forName(String 完整的类名)</font>，完整的类名包含包名。 

原始类型的class、包装类型的TYPE均可以获得Class对象。

```java
package com.byron4j.hightLevel.reflection;

public class ReflectionDemo {
	public static void main(String[] args) throws Exception{
		
		//方式一： 通过累的静态变量class获取Class对象
		Class concreteClass = ConcreteClass.class;
		
		//方式二：通过实例的getClass()方法获取Class对象
		concreteClass = new ConcreteClass(7).getClass();
		
		//方式三：
		concreteClass = Class.forName("com.byron4j.hightLevel.reflection.ConcreteClass");
		
		//打印类相关信息
		System.out.println(concreteClass.getCanonicalName());
		System.out.println(concreteClass.getName());
		
		
		/*++++++++++++++++++++++++++++++++++++++++++++++++
		 * 原始类型的class、包装类型的TYPE
		 * +++++++++++++++++++++++++++++++++++++++++++++++
		 */
		
		Class primative = boolean.class;
		System.out.println(primative.getCanonicalName());
		
		Class doubleClass = Double.TYPE;
		System.out.println(doubleClass.getName());
		
		//数组类型的class示例
		Class<?> arrayClass = Class.forName("[D");
		System.out.println(arrayClass.getCanonicalName());
		arrayClass = Class.forName("[B");
		System.out.println(arrayClass.getCanonicalName());
		arrayClass = Class.forName("[S");
		System.out.println(arrayClass.getCanonicalName());
		arrayClass = Class.forName("[C");
		System.out.println(arrayClass.getCanonicalName());
		arrayClass = Class.forName("[F");
		System.out.println(arrayClass.getCanonicalName());
		
		
		
	}
}

```

输出如下所示：

```
com.byron4j.hightLevel.reflection.ConcreteClass
com.byron4j.hightLevel.reflection.ConcreteClass
boolean
double
double[]
byte[]
short[]
char[]
float[]

```

Class的getCanonicalName()方法返回类的名称。在泛型中使用 java.lang.Class，可以帮助框架获取子类。


#### 获取超类Super Class

**<font color=red size=5><b>getSuperclass()</font>** 方法，返回类的超类(基类、父类)的class实例，如果该类是java.lang.Object、原始类型、接口则返回null。如果该class是数组形式，则该方法返回java.lang.Object。

```java
Class<?> superClass = Class.forName("com.byron4j.hightLevel.reflection.ConcreteClass").getSuperclass();
System.out.println(superClass); 
System.out.println(Object.class.getSuperclass());
System.out.println(String[][].class.getSuperclass());
```

输入如下：

```
class com.byron4j.hightLevel.reflection.BaseClass
null
class java.lang.Object
```

#### 获取公有的class

Class的<font color=red size=5><b>getClasses()</font> 方法可以获取class的所有继承的超类、接口和自己定义的<font color=red size=5><b>公有</font>类、接口、枚举等的数组形式。 

```java
Class[] classARR = concreteClass.getClasses();
System.out.println(Arrays.toString(classARR));
```

输出：

```java
[class com.byron4j.hightLevel.reflection.ConcreteClass$ConcreteClassPublicClass, 
class com.byron4j.hightLevel.reflection.ConcreteClass$ConcreteClassPublicEnum, 
interface com.byron4j.hightLevel.reflection.ConcreteClass$ConcreteClassPublicInterface, 
class com.byron4j.hightLevel.reflection.BaseClass$BaseClassInnerClass, 
class com.byron4j.hightLevel.reflection.BaseClass$BaseClassMemberEnum]
```

#### 获取自身声明的类

<font color=red size=5><b>**getDeclaredClasses()**</font>获取当前类型自身定义的所有类、接口，并不包含从父类继承过来的来、接口。

```java
Class[] declareClassARR = concreteClass.getDeclaredClasses();
System.out.println("Arrays.toString(declareClassARR));
```

输出：

```
[class com.byron4j.hightLevel.reflection.ConcreteClass$ConcreteClassDefaultClass, 
class com.byron4j.hightLevel.reflection.ConcreteClass$ConcreteClassDefaultEnum, 
class com.byron4j.hightLevel.reflection.ConcreteClass$ConcreteClassPrivateClass, 
class com.byron4j.hightLevel.reflection.ConcreteClass$ConcreteClassProtectedClass, 
class com.byron4j.hightLevel.reflection.ConcreteClass$ConcreteClassPublicClass, 
class com.byron4j.hightLevel.reflection.ConcreteClass$ConcreteClassPublicEnum,
interface com.byron4j.hightLevel.reflection.ConcreteClass$ConcreteClassPublicInterface]

```

#### 获取定义该class的类

class.<font color=red size=5><b>getDeclaringClass()</font>获取定义class的类。如果该类不是任何类或接口的成员，则返回null。

```java
/**================================================
		 * getDeclaringClass
		 * ================================================
		 */
		System.out.println(BaseClassInnerClass.class.getDeclaringClass());
System.out.println(Double.TYPE.getDeclaringClass());
```
该类BaseClassInnerClass是在BaseClass中定义的，于是输出：

```
class com.byron4j.hightLevel.reflection.BaseClass

null
```

#### 获取包名

<font color=red size=5><b>getPackage() </font>方法获取包的class实例。

```java
/*===========================================
* getPackage()
* ==========================================
*/
System.out.println(concreteClass.getPackage().getName());
```

输出：

```
com.byron4j.hightLevel.reflection
```

#### 获取类的修饰符


<font color=red size=5><b>getModifiers()</font>方法可以获取class实例的访问修饰符的个数。<font color=red size=5><b>java.lang.reflect.Modifier.toString()</font>可以获取class的修饰符的字符串形式。

```java
/*===========================================
		 * getModifiers()、Modifier.toString()
		 * ==========================================
		 */
		System.out.println(concreteClass.getModifiers());
		System.out.println(Modifier.toString(concreteClass.getModifiers()));
```

输出：

```
1
public
```


#### 获取类型参数

<font color=red size=5><b>getTypeParameters()</font>方法获取class的类型声明参数，如果有的话。比如集合框架的接口均制定了泛型。

```java
Arrays.asList(Class.forName("java.util.Map").getTypeParameters()).forEach(
				s -> { System.out.println(s); }
		);
```

输出：

> K
> V



#### 获取class实现的接口

<font color=red size=5><b>getGenericInterfaces()</font> 可以获取class已经实现的接口的数组形式，并包含泛型接口。 <font color=red size=5><b>getInterfaces()</font>方法会返回所有实现的接口，但是不包含泛型接口。

```java
/**=============================================
		 * getGenericInterfaces()、getInterfaces()
		 * =============================================
		 */
		Arrays.asList(concreteClass.getInterfaces()).forEach(
				s -> { System.out.println("com.byron4j.hightLevel.reflection.ConcreteClass实现的接口:" + s); }
		);
		System.out.println("========================================");
		Arrays.asList(concreteClass.getGenericInterfaces()).forEach(
				s -> { System.out.println("com.byron4j.hightLevel.reflection.ConcreteClass实现的接口:" + s); }
		);
		
		System.out.println("========================================");
		System.out.println("========================================");
		Arrays.asList(Class.forName("java.util.ArrayList").getInterfaces()).forEach(
				s -> { System.out.println("java.util.ArrayList实现的接口：" + s); }
		);
		System.out.println("========================================");
		Arrays.asList(Class.forName("java.util.ArrayList").getGenericInterfaces()).forEach(
				s -> { System.out.println("java.util.ArrayList实现的接口：" + s); }
		);
		
```

输出：
```
com.byron4j.hightLevel.reflection.ConcreteClass实现的接口:interface com.byron4j.hightLevel.reflection.BaseInterface
========================================
com.byron4j.hightLevel.reflection.ConcreteClass实现的接口:interface com.byron4j.hightLevel.reflection.BaseInterface
========================================
========================================
java.util.ArrayList实现的接口：interface java.util.List
java.util.ArrayList实现的接口：interface java.util.RandomAccess
java.util.ArrayList实现的接口：interface java.lang.Cloneable
java.util.ArrayList实现的接口：interface java.io.Serializable
========================================
java.util.ArrayList实现的接口：java.util.List<E>
java.util.ArrayList实现的接口：interface java.util.RandomAccess
java.util.ArrayList实现的接口：interface java.lang.Cloneable
java.util.ArrayList实现的接口：interface java.io.Serializable

```

#### 获取所有的public方法

<font color=red size=5><b>getMethods()</font>方法可以获取所有的public方法，包含父类、接口中继承来的public方法。

```java
/**=============================================
		 * getMethods()
		 * =============================================
		 */
		System.out.println("========================================");
		Arrays.asList(concreteClass.getMethods()).forEach(
				s -> { System.out.println("public类型的方法:" + s); }
		);
```

输出：

```
public类型的方法:public void com.byron4j.hightLevel.reflection.ConcreteClass.method1()
public类型的方法:public int com.byron4j.hightLevel.reflection.ConcreteClass.method2(java.lang.String)
public类型的方法:public int com.byron4j.hightLevel.reflection.ConcreteClass.method4()
public类型的方法:public int com.byron4j.hightLevel.reflection.ConcreteClass.method5(int)
public类型的方法:public static int com.byron4j.hightLevel.reflection.BaseClass.method5()
public类型的方法:public final void java.lang.Object.wait() throws java.lang.InterruptedException
public类型的方法:public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
public类型的方法:public final native void java.lang.Object.wait(long) throws java.lang.InterruptedException
public类型的方法:public boolean java.lang.Object.equals(java.lang.Object)
public类型的方法:public java.lang.String java.lang.Object.toString()
public类型的方法:public native int java.lang.Object.hashCode()
public类型的方法:public final native java.lang.Class java.lang.Object.getClass()
public类型的方法:public final native void java.lang.Object.notify()
public类型的方法:public final native void java.lang.Object.notifyAll()

```

#### 获取class的所有public构造器

<font color=red size=5><b>getConstructors()方法能够获取所有的public类型构造器</font>

```java
/**=============================================
		 * getConstructors()
		 * =============================================
		 */
		System.out.println("========================================");
		Arrays.asList(concreteClass.getConstructors()).forEach(
				s -> { System.out.println("public类型的构造器:" + s); }
		);
```

输出：

> public类型的构造器:public com.byron4j.hightLevel.reflection.ConcreteClass(int)

#### 获取所有的public属性(成员变量)

<font color=red size=5><b>getFields()</font>方法可以获取所有的public属性。包含父类、接口中的属性。

```java
/**=============================================
		 * getFields()
		 * =============================================
		 */
		System.out.println("========================================");
		Arrays.asList(concreteClass.getFields()).forEach(
				s -> { System.out.println("public类型的属性:" + s); }
		);
```

输出：

```
public类型的属性:public int com.byron4j.hightLevel.reflection.ConcreteClass.publicInt
public类型的属性:public static final int com.byron4j.hightLevel.reflection.BaseInterface.interfaceInt
public类型的属性:public int com.byron4j.hightLevel.reflection.BaseClass.baseInt

```

#### 获取所有的注解

<font color=red size=5><b>getAnnotations()</font>方法可以获取所有的注解。但是只有保留策略为**RUNTIME**的注解。

我们给  类加上注解@Deprecated。

```java
@Deprecated
public class ConcreteClass extends BaseClass implements BaseInterface{...}

/////////////////////////////////////
/**=============================================
		 * getAnnotations()
		 * =============================================
		 */
		System.out.println("========================================");
		Arrays.asList(concreteClass.getAnnotations()).forEach(
				s -> { System.out.println("注解:" + s); }
		);
```

输出：

> 注解:@java.lang.Deprecated()

## 反射在属性（成员变量）中的应用

反射API提供了几个方法可以在运行时分解类的成员变量以及设置其值。 

#### 获取public属性

除了前面的getFields()方法能获取全部public属性之外，还提供了一个获取指定属性名称的方法<font color=red size=5>getField()</font>。这个方法会在该class-->接口-->父类的顺序寻找指定的属性。

**场景一**：我们在ConcreteClass类、BaseClass类、BaseInterface接口分别增加一个属性：
public String name = "currClass---NAME";

public String name = "superClass---NAME";

public String name = "superInterface---NAME";


```java

System.out.println(concreteClass.getField("name"));

输出为：
public java.lang.String com.byron4j.hightLevel.reflection.ConcreteClass.name


```


**场景二**：我们在BaseClass类、BaseInterface接口分别增加一个属性：
public String name = "superClass---NAME";

public String name = "superInterface---NAME";


这时候获取的属性name是接口中的属性：
> public static final java.lang.String com.byron4j.hightLevel.reflection.BaseInterface.name

**场景三**：我们仅仅在BaseClass类增加一个属性：
public String name = "superClass---NAME";

这时候获取的属性name是父类中的属性：
> public java.lang.String com.byron4j.hightLevel.reflection.BaseClass.name

如果是获取不存在的属性，则出现异常：

System.out.println(concreteClass.getField("hello"));

输出：
> <font color=red size =4>java.lang.NoSuchFieldException: hello

#### 获取声明属性的类型

```java
try {
			System.out.println( concreteClass.getField("interfaceInt").getDeclaringClass() );
		} catch (NoSuchFieldException | SecurityException e) {
			
			e.printStackTrace();
		}
```

输出：
> interface com.byron4j.hightLevel.reflection.BaseInterface



#### 获取属性的类型

<font color=red size =5><b>getType()</font>方法返回属性的类型的class实例。

```
System.out.println(concreteClass.getField("interfaceInt").getType().getCanonicalName());
```

输出：
> int

#### Get/Set public类型的属性值

Field.get(Object) 获取该属性的值。

```java
Field field = concreteClass.getField("publicInt");
System.out.println("属性的类型："+field.getType());

ConcreteClass obj= new ConcreteClass(7);

System.out.println("获取属性值：" + field.get(obj));

field.set(obj, 77);
System.out.println("获取属性值：" + field.get(obj));
```

输出：
> 属性的类型：int
获取属性值：7
获取属性值：77


Field.get()返回的是一个Object类型，如果是原始类型则返回其包装类型。如果是final属性，set() 方法抛出**java.lang.IllegalAccessException**。


#### Get/Set private类型的属性值

java中在类之外是不能访问private变量的。但是通过反射可以关闭检查访问修饰符的机制。

```java
Field privateField = concreteClass.getDeclaredField("privateString");
		
System.out.println(privateField.get(obj));
```
输出，不能访问private的属性：
>  java.lang.IllegalAccessException: Class com.byron4j.hightLevel.reflection.ReflectionDemo2 can not access a member of class com.byron4j.hightLevel.reflection.ConcreteClass with modifiers "private"

设置可访问机制<font color=red size=5>Field.setAccessible(true);：
```java
		Field privateField = concreteClass.getDeclaredField("privateString");
		privateField.setAccessible(true);
		System.out.println(privateField.get(obj));

		privateField.set(obj, "新的私有属性值[value]");
		System.out.println(privateField.get(obj));
```

输出：
> private string
> 新的私有属性值[value]

## 与方法相关的反射方法

使用反射技术可以获得方法的信息以及调用执行它。我们来学习获得方法、调用方法并访问私有方法。

#### 获得public方法

我们可以使用 <font color=red size=5>getMethod()方法获的public class的方法，需要提供方法的名称、参数类型。如果class找不到指定的方法，则会继续向上从其父类中查找。

下面我们以一个获取HashMap 的put方法的例子来展示如何方法的参数类型、方法访问修饰符和返回类型。

```java
/*=========================================
		 * 方法
		 * ========================================
		 */
		
		Class mapClass = HashMap.class;
		Method mapPut = mapClass.getMethod("put", Object.class, Object.class);
		//获取参数类型
		System.out.println(Arrays.toString(mapPut.getParameterTypes()));
	
		//获取返回类型
		System.out.println(mapPut.getReturnType());
		
		//访问修饰符
		System.out.println(Modifier.toString(mapPut.getModifiers()));
```

输出：
> [class java.lang.Object, class java.lang.Object]
class java.lang.Object
public


#### 调用public方法

可以利用<font color=red size=5><b>Method.invoke() </font>方法调用指定的方法。

```java
//调用方法
Map<String, String> map = new HashMap<String, String>();
mapPut.invoke(map, "key", "val");
System.out.println(map);
```

输出：
> {key=val}

#### 调用private方法

我们可以使用<font color=red size=5><b>getDeclaredMethod()</font>方法获取私有方法，然后关闭访问限制，即可调用。 

```java
/**
* 访问私有方法
*/
Method method3 = BaseClass.class.getDeclaredMethod("method3", null);

method3.setAccessible(true);
//静态方法的调用对象可以传入null
method3.invoke(null, null);
```

输出：
> Method3


# 反射在构造器中的使用

我们可以使用<font color=red size=5><b>getConstructor()</font>方法获取指定的public构造器。

```java
/**
* 反射在构造器中的使用
*/
Constructor<?> constructor = ConcreteClass.class.getConstructor(int.class);

System.out.println(Arrays.toString(constructor.getParameterTypes())); 
	
Constructor<?> hashMapConstructor = HashMap.class.getConstructor(null);
System.out.println(Arrays.toString(hashMapConstructor.getParameterTypes())); 
		
```

输出：
> [int]
[]


#### 利用构造器初始化对象实例

我们可以利用constructor 实例的<font color=red size=5><b>newInstance() </font>方法获初始化实例。

```java
/**
* 初始化实例
*/

Object myObj = constructor.newInstance(10);
Method myObjMethod = myObj.getClass().getMethod("method1", null);
myObjMethod.invoke(myObj, null); //prints "Method1 impl."

HashMap<String,String> myMap = (HashMap<String,String>)hashMapConstructor.newInstance(null);
```
