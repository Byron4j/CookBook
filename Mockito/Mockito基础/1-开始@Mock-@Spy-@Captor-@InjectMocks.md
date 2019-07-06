# 开始@Mock-@Spy-@Captor-@InjectMocks

## 概览

Mockito 是Java中用于单元测试的模拟框架。

## 引入 pom 依赖

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-all</artifactId>
    <version>LATEST</version>
</dependency>
```


## 启用 Mockito

通过class参数（类、接口）创建一个mock对象，该对象与真实创建的对象有所区别。
使用 Mockito 类的一系列静态方法。

```java
public static <T> T mock(Class <T> classToMock)
```

### 编写一个 Mockito 示例

Mockito 需要依赖Junit。
```java
package org.byron4j.cookbook.mocketio.basic;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MockitoAnnotationTest {

    @Test
    public void whenNotUseMockAnnotation_thenCorrect() {
        // 创建一个mock出来的ArrayList对象
        List mockList = Mockito.mock(ArrayList.class);

        // 调用mock对象的方法
        mockList.add("one");
        //mockList.add("one");

        // 获取mock对象的实际方法，获取size，结果为0
        System.out.println("mockList.size(): " + mockList.size());
        // toString方法
        System.out.println("mockList's toString is: " + mockList);

        // 验证mock对象mockList的add方法是否被调用了一次
        Mockito.verify(mockList).add("one");
        assertEquals(0, mockList.size());

        // 当调用mockList.size()的时候，总是返回100
        Mockito.when(mockList.size()).thenReturn(100);

        assertEquals(100, mockList.size());
    }
}

```

运行输出结果为:

```
mockList.size(): 0
mockList's toString is: Mock for ArrayList, hashCode: 409962262
```

- 使用```List mockList = Mockito.mock(ArrayList.class);```创建一个mock出来的ArrayList对象mockList
- 调用mock对象的方法```mockList.add("one");```
- 然后调用```mockList.size()```结果为0，表明```mockList.add("one");```代码仅仅表示发生了add行为本身，并不会对mockList的其他行为产生影响。
- verify方法，验证mock对象mockList的add方法是否被调用了一次
```
Mockito.verify(mockList).add("one");
assertEquals(0, mockList.size());
```

- 当调用mockList.size()的时候，总是返回100
```java
Mockito.when(mockList.size()).thenReturn(100);
assertEquals(100, mockList.size());
```




#### verify 方法

验证包含的行为(方法)发生过一次(被调用一次)，即verify(mock, times(1))，例如： ```verify(mock).someMethod("some arg");```。等效于 ```verify(mock, times(1)).someMethod("some arg");```

```java
Mockito.verify(mockList).add("one");
```

等效于

```java
Mockito.verify(mockList, Mockito.times(1)).add("one");
```

```Mockito.times(1)``` 参数1表示期望执行的次数是1。

###### verify 方法传入两个参数：mock对象、验证模式

```java
public static <T> T verify(T mock,  VerificationMode mode);
```

>Mockito.times(int wantedNumberOfInvocations) 可以得到一个VerificationMode对象，实际调用了 ```VerificationModeFactory.times(wantedNumberOfInvocations)```获取到一个Times对象：```new Times(wantedNumberOfInvocations)```,Times实现了VerificationMode接口。
>
>- 参数一: mock 对象，必须的
>
>- 参数二： 验证模式：times(x), atLeastOnce() 或者 never() 等；如果是times(1)则可忽略该参数
>
>times方法调用栈如下：

```java
org.mockito.Mockito#times(int wantedNumberOfInvocations)
	org.mockito.internal.verification.VerificationModeFactory#times(int wantedNumberOfInvocations)
		org.mockito.internal.verification.Times(int wantedNumberOfInvocations)
```

#### when 方法

Mockito.when方法定义如下:

```java
public static <T> OngoingStubbing<T> when(T methodCall)
```

when方法需要传递一个  mock对象的方法 的调用，例如本例中我们传递了mock对象mockList的mockList.size()方法的调用。
when方法会留一份存根，在我们希望模拟在特定情况下返回特定的返回值时，回调用它。
简单的意图就是： ```当x方法调用的时候，就返回y```。

示例：

- ```when(mock.someMethod()).thenReturn(10);``` : 调用方法时返回10

- ```when(mock.someMethod(anyString())).thenReturn(10);``` ： 灵活参数

- ```when(mock.someMethod("some arg")).thenThrow(new RuntimeException());``` ： 调用方法时，抛出一个异常

 
- ```when(mock.someMethod("some arg")).thenThrow(new RuntimeException()).thenReturn("foo");``` ： 连续调用不同的行为

- ```when(mock.someMethod("some arg")).thenReturn("one", "two");``` ： 连续的存根，第一次调用返回"one",第二次以及之后的调用返回"two"
 
- ```when(mock.someMethod("some arg")).thenReturn("one").thenReturn("two");``` ： 和上面一条等同效果

 
- ```when(mock.someMethod("some arg")).thenThrow(new RuntimeException(), new NullPointerException();``` ： 连续存根，抛异常

```java
@Test
public void whenTest() {
    List mock = Mockito.mock(List.class);
    Mockito.when(mock.size()).thenReturn(-1);
    System.out.println("mock.size():" + mock.size());



    // 连续存根
    Mockito.when(mock.size()).thenReturn(1).thenReturn(2).thenReturn(3);
    for(int i=1; i <= 5; i++){
        System.out.println("=====连续存根方式1：=====： " + mock.size());
    }

    Mockito.when(mock.size()).thenReturn(1,2, 3);
    for(int i=1; i <= 5; i++){
        System.out.println("#####连续存根方式2：#####： " + mock.size());
    }

    // 模拟异常
    Mockito.when(mock.size()).thenThrow(new RuntimeException(), new NullPointerException());
    try{
        mock.size();
    }catch (Exception e){
        System.out.println(e);
    }
    try{
        mock.size();
    }catch (Exception e){
        System.out.println(e);
    }

}
```

运行输出：

```java
mock.size():-1
=====连续存根方式1：=====： 1
=====连续存根方式1：=====： 2
=====连续存根方式1：=====： 3
=====连续存根方式1：=====： 3
=====连续存根方式1：=====： 3
#####连续存根方式2：#####： 1
#####连续存根方式2：#####： 2
#####连续存根方式2：#####： 3
#####连续存根方式2：#####： 3
#####连续存根方式2：#####： 3
java.lang.RuntimeException
java.lang.NullPointerException
```


### 启用 Mockito 的注解功能


#### @RunWith(MockitoJUnitRunner.class) 开启注解功能

使用 ```@RunWith(MockitoJUnitRunner.class)``` 在类上开启Mockito注解功能。

```java
@RunWith(MockitoJUnitRunner.class)
public class MockitoAnnotationStartup {
}
```


#### @Mock注解

通过 @Mock注解可以得到mock对象，等价于 Mockito.mock(class)。

```java
    /**注解得到的mock对象*/
    @Mock
    List<String> mockList;
    
    等价于
    
    List<String> mock = Mockito.mock(List.class);
```

示例如下：

```java
package org.byron4j.cookbook.mocketio.basic;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MockitoAnnoTest extends MockitoAnnotationStartup{

    /**注解得到的mock对象*/
    @Mock
    List<String> mockList;

    @Test
    public void testRaw(){
        List<String> mock = Mockito.mock(List.class);
        mock.add("one");
        mock.add("one");
        Mockito.verify(mock, Mockito.times(2)).add("one");

        Mockito.when(mock.size()).thenReturn(100);
        assertEquals(100, mock.size());


    }

    @Test
    public void testAnno(){
        mockList.add("one");
        mockList.add("one");
        Mockito.verify(mockList, Mockito.times(2)).add("one");

        Mockito.when(mockList.size()).thenReturn(100);
        assertEquals(100, mockList.size());


    }
}

```

#### @Spy 注解

和 @Mock 注解类似，还有 @Spy 注解。spy是密探间谍的意思，假冒的。

```java
List<String> mock = Mockito.spy(List.class);
```

使用注解

```java
@Spy
List<String> spyList;
```

#### @Captor 注解（参数捕获器）

参数捕获器 ArgumentCaptor 对应注解 @Captor。


原始方式创建一个参数捕获器：

```java
@Test
public void whenNotUseCaptorAnnotation_thenCorrect() {
    List mockList = Mockito.mock(List.class);
    ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);
 
    mockList.add("one");
    Mockito.verify(mockList).add(arg.capture());
 
    assertEquals("one", arg.getValue());
}
```


使用@Captor注解创建一个参数捕获器：

```java
@Mock
List mockedList;
 
@Captor
ArgumentCaptor argCaptor;
 
@Test
public void whenUseCaptorAnnotation_thenTheSam() {
    mockedList.add("one");
    Mockito.verify(mockedList).add(argCaptor.capture());
 
    assertEquals("one", argCaptor.getValue());
}
```

- ```ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);``` : 创建一个参数捕获器
- ```Mockito.verify(mockedList).add(argCaptor.capture());``` : 在一个验证中使用捕获器捕获方法add的参数; capture 方法必须在一个验证中。
- ```argCaptor.getValue()``` ： 获取参数捕获器捕获到的参数



#### @InjectMocks 注解

```@InjectMocks``` 注解可以将mock的属性自动注入到测试对象中。

```java
@Mock
Map<String, String> wordMap;

@InjectMocks
MyDictionary myDictionary = new MyDictionary();


@Test
    public void testInjectMocks(){
        Mockito.when(wordMap.get("aWord")).thenReturn("aMeaning");

        assertEquals("aMeaning", myDictionary.getMeaning("aWord"));

        System.out.println(myDictionary.getMeaning("aWord"));
    }

    class MyDictionary{
        Map<String, String> wordMap;

        public String getMeaning(String word){
            return wordMap.get(word);
        }
    }
```  

- MyDictionary 类存在属性 wordMap ： ```Map<String, String> wordMap;```
- Mock 一个变量名为 wordMap : 

```java
@Mock
Map<String, String> wordMap;
```
- 使用 @InjectMocks 注解标记 ：

```java
@InjectMocks
MyDictionary myDictionary = new MyDictionary();
```

则会将mock出来的对象wordMap注入到myDictionary实例的同名属性中。


>**注意事项：**
>
>使用注解最小化重复编写创建mock对象的代码
>
>使用注解让测试案例可读性更好
>
>使用 @InjectMocks 注解注入 @Spy 和 @Mock 对象
