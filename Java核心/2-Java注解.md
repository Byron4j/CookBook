# Java核心（二）注解

## 注解是什么？

注解是一种特殊的接口，注解继承自 ```java.lang.annotation.Annotation```。

```java
/**
The common interface extended by all annotation types. 
Note that an interface that manually extends this one does not define an annotation type. 
Also note that this interface does not itself define an annotation type. 
More information about annotation types can be found in section 9.6 of The Java™ Language Specification. 
The reflect.AnnotatedElement interface discusses compatibility concerns when evolving an annotation type from being non-repeatable to being repeatable.
*/
public interface Annotation {
    ...
}
```

```Annotation``` 接口的文档描述：

- 所有注解类型都会继承 Annotation 接口
- 手工显示继承 Annotation 接口，不会定义成一个注解类型
- Annotation 本身并不会定义成一个注解类型


### 编写一个注解

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Demo {
}

```

编译： javac .\Demo.java
查看字节码： javap .\Demo.class：

```class
       Compiled from "Demo.java"
       public interface org.byron4j.cookbook.javacore.anno.Demo extends java.lang.annotation.Annotation {
```

通过字节码，我们可以看到注解继承自 Annotation 接口。


## 注解的用处

我们要学习一个东西，肯定要知道它的作用。相信没有 Java 程序猿不知道 Spring 框架的，Spring 框架中定义了大量的注解，基于注解，我们可以做到几乎零配置的开发商业项目，如 SpringBoot 就将原来 Spring 使用 XML 进行配置全部转换为使用注解达到相同的功效。
如以下是部分注解：

- @RestController
- @RequestMapping
- @Configuration
- @SpringBootApplication
- @ConfigurationProperties


## 了解注解

注解可以分为 **元注解**、**内置注解**、**用户自定义注解**

### 元注解

所谓元注解就是，一种基本注解，可以应用在其它的注解上。可以这样理解，元注解是用于标记注解的注解。

元注解有：
- java.lang.annotation.Retention
- java.lang.annotation.Target
- java.lang.annotation.Documented
- java.lang.annotation.Inherited
- java.lang.annotation.Repeatable
- java.lang.annotation.Native

#### @Retention 

Retention 是保留的意思，表明注解产生的时间范围。其值是 ```java.lang.RetentionPolicy``` 枚举。

- RetentionPolicy.SOURCE : 只在源代码级别保留有用，在编译期就丢弃了
- RetentionPolicy.CLASS ： 在编译期保留有效，在运行期（JVM中）开始丢弃；这是默认的保留策略
- RetentionPolicy.RUNTIME ： 在编译期、运行其都保留有效，所以可以在反射中使用

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface Demo {
}
```

#### @Documented

@Documented 表明注解的类型将会包含到Javadoc中去。

#### @Target

@Target 标明注解使用约束的应用上下文，是数组形式，可以标记在多个范围中使用。值由 ```java.lang.annotation.ElementType``` 指定。

java.lang.annotation.ElementType 的可用值如下：

- TYPE ： 类、接口、注解、枚举的声明中
- FIELD ： 成员变量，包含枚举的常量的声明中
- METHOD ： 方法的声明中
- PARAMETER ： 正式的参数的声明中
- CONSTRUCTOR ： 构造器的声明中
- LOCAL_VARIABLE ： 局部变量的声明中
- ANNOTATION_TYPE ： 注解类型的声明中
- PACKAGE ： 包的声明中
- TYPE_PARAMETER ： 类型参数的声明中（since JDK8）
- TYPE_USE ： 类型的使用（since JDK8）

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
public @interface Demo {
}

```

#### @Inherited

Inherited 字面意思是继承得意思，```@Inherited`` 这个注解将会被自动被继承。
**要求子类没有被任何注解标记。**
只会在extends时才有效，实现接口是不生效的。给个例子解释下：

```java
@Inherited
public @interface Demo {
}

@Demo
public class SuperClass{
    
}

public class ChildClass extends SuperClass{
    
}
```

父类 SuperClass 被注解 @Demo 标记了，子类 ChildClass 继承了父类 SuperClass 且子类没有被任何注解标记过，则子类会继承父类的被元注解@Inherited标记的注解 @Demo。

等效于：

```java
@Demo
public class ChildClass extends SuperClass{
    
}
```

#### @Repeatable

该元注解标明声明的注解是可以多次使用的，@Repeatable 属性要求是 注解的class。
以下是一个 Demo注解，表示用户角色的等级，SSS---》B 共5个等级。

**注意：** 使用了 @Repeatable 标记了 Demo注解，指定了 Role.class 作为属性值，并且 Role 注解必须属性名为 value， 且类型为 Demo 数组形式。

示例如下：

```java
@Repeatable(Role.class)
public @interface Demo {
    String role();
}


public @interface Role {
    Demo[] value();
}


public @interface Td {
}

@Demo(role="SSS")
@Demo(role="SS")
@Demo(role="S")
@Demo(role="A")
@Demo(role="B")
public class FK1 {
}


@Demo(role="SSS")
@Demo(role="SS")
@Demo(role="S")
@Demo(role="A")
@Demo(role="B")
@Td // 错误
@Td // 错误  Td注解不能重复出现
public class FK1 {
}
```

- **没有被@Repeatable标记的注解不能多次出现。**

### 注解的属性

注解只有变量（属性），没有方法， 注解的属性是以 <font color=red>**无参方法的形式**</font> 声明的。

```java
public @interface Demo {
    String role();
}
```

@Demo 注解声明了一个属性 role。

#### 属性的默认值

属性后面使用 ```default``` 关键字可以给属性设置默认值。

```java
public @interface Demo {
    String role() default "B";
}
```

#### 注解的使用


如果只有一个名为 value 的属性，则使用注解的时候可以直接在圆括号写属性值。

```java
public @interface Due {
    String value();
}

@Due("Hi")
public class FK1 {
}

// 等效于

@Due(value="Hi")
public class FK1 {
}
```

没有属性的注解，使用时仅仅标记下即可。

```java
public @interface Due {
}

@Due // 仅仅标记下即可
public class FK1 {
}
```


## Java 内置注解

- @Deprecated ： 表示废弃，在编译期会发出警告。

```java
public class AnnoDemo {
    @Deprecated
    private static void sayHello(){

    }

    public static void main(String[] args){
        AnnoDemo.sayHello();
    }
}
```

- @FunctionalInterface ： 函数式接口：一个具有一个方法的普通接口。
- @Override ： 实现类要重写父类或者接口的方法
- @SafeVarargs ： 参数安全类型注解，告诉开发者不要用参数做一些不安全的操作
- @SuppressWarnings ： 阻止编译器发出告警，比如调用了使用了 @Deprecated 标记的方法编译器会发出警告，可以使用 @SuppressWarnings 压制警告
  
  可以通过 javac -X 命令查看可以压制的警告值：
  ```sh
  C:\Users\BYRON.Y.Y>javac -X
    -Xlint                     启用建议的警告
    -Xlint:{all,auxiliaryclass,cast,classfile,deprecation,dep-ann,divzero,empty,fallthrough,finally,options,overloads,overrides,path,processing,rawtypes,serial,static,try,unchecked,varargs,-auxiliaryclass,-cast,-classfile,-deprecation,-dep-ann,-divzero,-empty,-fallthrough,-finally,-options,-overloads,-overrides,-path,-processing,-rawtypes,-serial,-static,-try,-unchecked,-varargs,none} 启用或禁用特定的警告
  ```
  @SuppressWarnings 的部分值介绍：
  - all ： @SuppressWarnings("all") ，会压制所有的警告
  - cast ： 压制类造型转换警告
  - deprecation ： 压制废弃的警告，比如可能使用了 @Deprecated
  - divzero ： 压制除数为0的警告
  - unchecked ： 压制没有指定泛型的集合表达式
  - fallthrough : 压制 switch警告，比如某个case没有break语句
  - serial ： 压制 实现 Serializable 接口但是没有声明 serialVersionUID 属性的警告
  - finally ：压制没有正确使用finally的警告，比如没有捕获异常，编译器会发出警告：
  
  ```java
    @SuppressWarnings("finally")
    public String finallyTest(String str)
    {
        try
        {
            str+=".";
        }
        finally
        {
            return str.toUpperCase();
        }
    }
  ```
  - overrides ： 压制没有覆盖父类的方法的警告





## 让注解有用武之地

注解仅仅是用作标记，要想它真实发挥作用，式利用Java反射机制编写注解解析器，用作业务需求。

### 注解与反射(java.lang.reflect包下)

- 可以通过 ```java.lang.reflect.Class``` 的 ```isAnnotationPresent()``` 得知是否存在注解。
- 可以通过 ```<T extends Annotation> T getAnnotation(Class<T> annotationClass)``` 方法获取注解对象
- 可以通过 ```Annotation[] getAnnotations()``` 方法获取注解列表

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ByronAnno {
    String value() default "ok";
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ByronAnno {
    String value() default "ok";
}


```

运行输出：```注解值为：类```


### 注解生产案例

最后以一个生产案例的使用，来结束对注解的介绍。

案例： http接口中，请求参数是字符串形式，将请求参数转换为请求实体类。对参数进行校验时，需要检查某些字段是否为空，以及整型数值的大小校验。


#### ValidateVal 注解类
```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateVal {

	String length() default "";

	boolean isBeNull() default false;

	boolean isNotcheckDecimal() default false;// 验证小数

	int isJudgeLength() default 0; // 0 :不判断 1:定值 2:区间

	int minLength() default 0;

	int maxLength() default 20;

	int minValue() default 0;

	int maxValue() default Integer.MAX_VALUE;

	String expression() default "";

	String errorMsg() default "";

	int neeDivided() default 1; // 是否需要倍数
	//是否校验数字，是=true,否=false
	boolean isCheckNumber() default false;

}

```


#### 用于解析 @ValidateVal 注解的解析方法

```java
protected <T> T validateAndConvert(String param, Class<T> cla) throws Exception {

		T t = JSONUtils.json2Object(param, cla);
		if (t == null) {
			// 抛出异常
		}
		Field[] fields = t.getClass().getDeclaredFields();
		Field[] parentFields = t.getClass().getSuperclass().getDeclaredFields();
		
		List<Field> allFields = Lists.newArrayList(fields);
		allFields.addAll(Arrays.asList(parentFields));
		
		for (Field f : allFields) {
			f.setAccessible(true);
			ValidateVal an = f.getAnnotation(ValidateVal.class);
			String fieldName = f.getName();
			if (an == null) {
				String value = String.valueOf(f.get(t));
				value = value.trim();
				if (f.getType().equals(String.class)) {
					f.set(t, value);
				}
				if (value == null || value.equals("") || value.equals("null")) {
					// 抛出异常
				}
			} else {
				if (f.getType().equals(String.class)) {
					String value = null;

					if (f.get(t) != null) {
						value = String.valueOf(f.get(t));
						value = value.trim();
						f.set(t, value);
					}

					if (!an.isBeNull()) {
						if (value == null || value.equals("") || value.equals("null")) {
							
							// 抛出异常
						}
					} else {// 为空串置为null
						if (value == null || value.equals("") || value.equals("null")) {
							f.set(t, null);
						}
					}

					if (!an.expression().equals("")) {
						Pattern pattern = Pattern.compile(an.expression());
						Matcher matcher = pattern.matcher(value);
						if (!matcher.matches()) {
							// 抛出异常
						}
					}

					if (an.isJudgeLength() == 1) { // 定值
						String[] lengthArr = an.length().split(",");
						boolean in = false;
						for (int i = 0; i < lengthArr.length; i++) {
							if (value.length() == Integer.parseInt(lengthArr[i])) {
								in = true;
							}
						}
						if (!in) {
							// 抛出异常
						}
					} else if (an.isJudgeLength() == 2) {
						int min = an.minLength();
						int max = an.maxLength();
						if (value.length() < min || value.length() > max) {
							// 抛出异常
						}
					}
				} else if (f.getType().equals(Integer.class)) {
					
					
					if (f.get(t) == null) {
						if (an.isBeNull()) {
							f.set(t, null);
							continue;
						} else {
							// 抛出异常
						}
					}
					Integer value = Integer.valueOf(f.get(t).toString());
					if (an.neeDivided() != 1 && value % an.neeDivided() != 0) {
						// 抛出异常
					}

					if (value < an.minValue() || value > an.maxValue()) {
						// 抛出异常
					}
				}
			}
		}
		return t;
	}
```


参考资料：

- SuppressWarnings values: http://blog.bangbits.com/2008/05/suppresswarnings-annotations.html

