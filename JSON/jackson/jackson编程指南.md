# Jackson编程指南

## 概述
Jackson 是一个操作json的库。
Jackson 提供了 writeValue() 和 readValue() 系列方法在javabean和json串之间进行转换。
github地址： https://github.com/codehaus/jackson
相关文档可以参考： https://github.com/FasterXML/jackson-docs

## 使用步骤
- 引入jar包（或者maven依赖）
    - jar包：jackson-annotations-2.2.3.jar、jackson-core-2.2.3.jar、jackson-databind-2.2.3.jar
    - maven依赖
        ```xml
          <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.2.3</version>
          </dependency>
          <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>2.2.3</version>
          </dependency>
          <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.2.3</version>
          </dependency>
        ```
- 创建 `ObjectMapper`（映射器、数据绑定器、编解码器）
    - 该映射器（或数据绑定器或编解码器）提供用于在Java对象（JDK的实例提供的核心类，bean）与匹配的JSON构造之间进行转换的功能。它底层使用`JsonParser`和`JsonGenerator`的实例来实现JSON的实际读取/写入。
      主要的编程API是在抽象类`ObjectCodec`中定义的。
      
- 操作读写（readValue()、writeValue()）
    - readValue() ： 读取json串转换为java对象
        - 读最常用的方法：==readValue(String content, Class<T> valueType)==
    - writeValue(): 将java对象转换为json串
        - 写最常用的方法：==String writeValueAsString(Object value)==
        
## 场景

### 1. 数组和json串的互相转换
代码块
```java
System.out.println("===========数组和json串的互相转换===========");
int[] arr = new int[]{1,2,3};
System.out.println("数组-->json串:" + objectMapper.writeValueAsString(arr));
System.out.println("json串-->数组:" + Arrays.toString(objectMapper.readValue("[1,2,3]", Object[].class)));

```
运行结果
```
===========数组和json串的互相转换===========
数组-->json串:[1,2,3]
json串-->数组:[1, 2, 3]
```



### 2. map和json串的互相转换

代码块
```java
// 2. map和json串的互相转换
System.out.println("===========map和json串的互相转换===========");
Map<String, Object> map = new HashMap<>();
map.put("aKey", "aVal");
map.put("bKey", 200);
map.put("cKey", new User());
System.out.println("map-->json串:" + objectMapper.writeValueAsString(map));
String jsonStr = "{\"bKey\":200,\"cKey\":{\"id\":0,\"username\":null,\"password\":null,\"email\":null,\"phone\":null},\"aKey\":\"aVal\"}";
System.out.println("json串-->map:" + objectMapper.readValue(jsonStr, Map.class));
```

运行结果
``` 
===========map和json串的互相转换===========
map-->json串:{"bKey":200,"cKey":{"id":0,"username":null,"password":null,"email":null,"phone":null},"aKey":"aVal"}
json串-->map:{bKey=200, cKey={id=0, username=null, password=null, email=null, phone=null}, aKey=aVal}
```


### 3. list和json串的互相转换

代码块
```java
System.out.println("===========list和json串的互相转换===========");
List<String> list = new ArrayList<>();
list.add("zs");
list.add("ls");
list.add("ww");
System.out.println("list-->json串:" + objectMapper.writeValueAsString(list));
jsonStr = "[\"zs\",\"ls\",\"ww\"]";
System.out.println("json串-->list:" + objectMapper.readValue(jsonStr, ArrayList.class));
```
运行结果
``` 
===========list和json串的互相转换===========
list-->json串:["zs","ls","ww"]
json串-->list:[zs, ls, ww]
```


### 4. javabean和json串的互相转换

代码块

```java
System.out.println("===========javabean和json串的互相转换===========");
User user = new User();
user.setId(1);
user.setUsername("zs");
user.setPassword("123456");
user.setEmail("zs@126.com");
user.setPhone("13333333333");
System.out.println("javabean-->json串:" + objectMapper.writeValueAsString(user));
jsonStr = "{\"id\":1,\"username\":\"zs\",\"password\":\"123456\",\"email\":\"zs@126.com\",\"phone\":\"13333333333\"}";
System.out.println("json串-->javabean:" + objectMapper.readValue(jsonStr, User.class));

```

运行结果
``` 
===========javabean和json串的互相转换===========
javabean-->json串:{"id":1,"username":"zs","password":"123456","email":"zs@126.com","phone":"13333333333"}
json串-->javabean:User{id=1, username='zs', password='123456', email='zs@126.com', phone='13333333333'}
```


### 5. 写json串到文件、从文件中读取到对象

代码块

```java
System.out.println("===========写json串到文件、从文件中读取到对象===========");
// 写对象到文件里面
objectMapper.writeValue(new File("user.json"), user);
// 从文件中读取数据到对象中
User user1 = objectMapper.readValue(new File("user.json"), User.class);
System.out.println(user1);

```

运行结果

``` 
===========写json串到文件、从文件中读取到对象===========
User{id=1, username='zs', password='123456', email='zs@126.com', phone='13333333333'}
```

### 完整的代码示例

```java
public class JacksonTest {
    @Test
    public void test() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        // 查看版本号
        System.out.println("objectMapper.version():" + objectMapper.version());


        // 1. 数组和json串的互相转换
        System.out.println("===========数组和json串的互相转换===========");
        int[] arr = new int[]{1,2,3};
        System.out.println("数组-->json串:" + objectMapper.writeValueAsString(arr));
        System.out.println("json串-->数组:" + Arrays.toString(objectMapper.readValue("[1,2,3]", Object[].class)));

        // 2. map和json串的互相转换
        System.out.println("===========map和json串的互相转换===========");
        Map<String, Object> map = new HashMap<>();
        map.put("aKey", "aVal");
        map.put("bKey", 200);
        map.put("cKey", new User());
        System.out.println("map-->json串:" + objectMapper.writeValueAsString(map));
        String jsonStr = "{\"bKey\":200,\"cKey\":{\"id\":0,\"username\":null,\"password\":null,\"email\":null,\"phone\":null},\"aKey\":\"aVal\"}";
        System.out.println("json串-->map:" + objectMapper.readValue(jsonStr, Map.class));
        // 3. list和json串的互相转换
        System.out.println("===========list和json串的互相转换===========");
        List<String> list = new ArrayList<>();
        list.add("zs");
        list.add("ls");
        list.add("ww");
        System.out.println("list-->json串:" + objectMapper.writeValueAsString(list));
        jsonStr = "[\"zs\",\"ls\",\"ww\"]";
        System.out.println("json串-->list:" + objectMapper.readValue(jsonStr, ArrayList.class));

        // 4. javabean和json串的互相转换
        System.out.println("===========javabean和json串的互相转换===========");
        User user = new User();
        user.setId(1);
        user.setUsername("zs");
        user.setPassword("123456");
        user.setEmail("zs@126.com");
        user.setPhone("13333333333");
        System.out.println("javabean-->json串:" + objectMapper.writeValueAsString(user));
        jsonStr = "{\"id\":1,\"username\":\"zs\",\"password\":\"123456\",\"email\":\"zs@126.com\",\"phone\":\"13333333333\"}";
        System.out.println("json串-->javabean:" + objectMapper.readValue(jsonStr, User.class));

        System.out.println("===========写json串到文件、从文件中读取到对象===========");
        // 写对象到文件里面
        objectMapper.writeValue(new File("user.json"), user);
        // 从文件中读取数据到对象中
        User user1 = objectMapper.readValue(new File("user.json"), User.class);
        System.out.println(user1);
    }

}
```
    
    
此篇仅仅介绍了jackson的基础使用。
