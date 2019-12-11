---
typora-copy-images-to: img
---



# XML介绍

![1576049327234](img\1576049327234.png)

## XML概述

XML是一种可扩展标记语言，也可以认为是一种数据交换格式。

- 可扩展： 语法格式约束不是很严格，用户可扩展性、自定义特性更强。
- 标记语言： 语法主要由标签组成。
- 数据交换格式：可以用作为客户端、服务端数据传输的数据格式。



## XML语法

文件以 .xml 结尾。

### XML文档声明

xml文档声明在文件首行编写以下内容：

```xml
<?xml version="1.0" ?>
```

文档声明的属性：

- ==version==：版本号 固定值 1.0；
- ==encoding==: 指定文档的编码，一般使用UTF-8编码。默认值为 iso-8859-1；  
- standalone：指定文档是否独立  yes 或 no(不做了解)

那么我们的xml文件声明（第一行）一般都可以这样写：==<?xml version="1.0" encoding="UTF-8"?>==

```xml
<?xml version="1.0" encoding="UTF-8"?>
```

### 标签（元素）

XML里面的标签也叫元素，最顶层的标签也叫根标签，XML文档中必须有且只能有一个根元素。

标签的特征:astonished:

- 标签由开始标签、结束标签组成，要闭合。

- 有文本内容的标签写法：

  ```xml
  <name>张三</name>
  ```

- 没有文本内容的标签写法：

  ```xml
  <nothing/>
  ```

- 标签可以嵌套，但是不能任意交叉嵌套

  - 正确嵌套使用:yum:

    ```xml
    <people>
    	<name>张三</name>
        <age>18</age>
    </people>
    ```

    

  - 错误的示范 :non-potable_water:

    ```xml
    <!--这是一个错误的示范：标签不能交叉嵌套-->
    <people>
        <name>
        </people>
    </name>
    ```

- 标签命名要规范

  - XML的标签名是区分大小写的
  - 不能以数字开头
  - 不能含空格，特殊字符如 @ 、%等符号



### 属性

标签可以拥有属性。

- 属性写法：  `属性名="属性值"`

  - 以下展示了一个属性height表示身高

  ```xml
  <people height="180cm">
      <name>张三</name>
  </people>
  ```

- 同一个标签里面不能出现多个同名属性 :non-potable_water:

  ```xml
  <!--这是一个错误的示范： 同一个标签里只能有一个相同的属性-->
  <people height="180cm"  height="170cm">
      <name>张三</name>
  </people>
  ```

  

### 注释

XML的注释写法：  <!---->

-  IDEA快捷键： Ctrl+Shift+/
- 注释不能嵌套，即注释里面不能再有注释
- 文档声明的前面也不能有注释

## 一个XML示例

以下是一个表示书籍的一个XML文档示例：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--books表示书籍,复数形式-->
<books>
    <!--book表示一本书-->
	<book>
    	<name>诗经</name>
        <price>2.5</price>
    </book>
    <!--star属性表示星标-->
    <book star="yes">
    	<name>Java从入门到放弃</name>
        <price>99</price>
    </book>
    <book>
    	<name>MySQL删库跑路</name>
        <price>30</price>
    </book>
</books>
```

