# Lua 语言入门指南


## 安装 Lua 环境

### Linux 环境安装

Linux & Mac上安装 Lua 安装非常简单，只需要下载源码包并在终端解压编译即可，本文使用了5.3.0版本进行安装

```shell
curl -R -O http://www.lua.org/ftp/lua-5.3.0.tar.gz
tar zxf lua-5.3.0.tar.gz
cd lua-5.3.0
make linux test
make install
```

### Mac OS 环境安装

```shell
curl -R -O http://www.lua.org/ftp/lua-5.3.0.tar.gz
tar zxf lua-5.3.0.tar.gz
cd lua-5.3.0
make macosx test
make install
```

## 第一个Lua程序

在Cent OS 的 Linux环境下演示：

```shell
echo 'print("Hello,Lua!")' > hello.lua

# 执行 lua hello.lua
lua hello.lua
# 输出结果为：
Hello,Lua!
```

### 交互式编程

Lua提供了交互式编程模式。我们可以在命令行中输入程序并立即查看效果。

Lua 交互式编程模式可以通过命令 **lua -i** 或者 **lua** 来启用：

```shell
lua -i
Lua 5.1.4  Copyright (C) 1994-2008 Lua.org, PUC-Rio
> 
```

在命令行中输入如下命令

```lua
> print("Hello,Lua!")

# 输出结果为:
Hello,Lua!
```

### 脚本式编程

将lua脚本保存到 .lua 的文件中，再使用 lua 执行该脚本文件。

```lua
echo 'print("Nice to meet you!")' > nice.lua
$ cat nice.lua 
print("Nice to meet you!")
$ lua nice.lua 
Nice to meet you!
```

## Lua 的注释

### 单行注释

2个减号 **--** 用以表明是单行注释

### 多行注释

```lua
--[[
        多行注释内容1
        多行注释内容2
--]]
```

## 标示符

Lua 标示符用于定义一个变量。
标示符以一个字母A 到 Z 或 a 到 z 或下划线 _开头后加上0个或多个字母，下划线，数字(0到9)。

**最好不用下划线加大写字母开头的标示符，因为Lua的保留字也是这样的形式。**

Lua 不允许使用特殊字符如 @，$，和 % 来定义标示符。
Lua区分大小写。

## Lua 关键字

Lua 关键字，不允许用户自定义为标示符。

|||||
|----|----|----|----|
|and|break|do|else|
|elseif|end|false|for|
|function|if|in|local|
|nil|not|or|repeat|
|return|then|true|until|
|while||||

一般约定，以下划线开头连接遗传大写字母的名字（比如 **_VERSION**）是Lua内部全局变量的保留字。
```lua
> print(_VERSION)
Lua 5.1
```

## 全局变量

默认情况下，变量总是认为全局的。
全局变量不需要声明，给一个变量赋值后即创建了这个全局变量，访问一个没有初始化的全局变量也不会出错，只不过得到的结果是空： nul。

```lua
> print(a)
nil
> a = 10
> print(a)
10
```

**如果想删除一个全局变量，将其赋值为nil即可。**

```lua
> a = nil
> print(a)
nil
```


## Lua 数据类型

Lua 有8个基本类型：

|数据类型|描述|
|----|:----:|
|nil|是一个空值|
|boolea|false、true|
|number|表示双精度的浮点数|
|string|字符串，由一堆双引号或者单引号包围|
|function|由C或者Lua编写的函数|
|userdate|表示任意存储在变量中的C数据结构|
|thread|表示执行的独立线程，用于执行协同程序|
|table|Lua中的表是一个关联数组，数组的索引可以是数字或者字符串。最简单的表是{}，表示是一个空表|

### 使用type()函数测试给定变量或值得数据类型

```lua
> type("Hello World!")
> print(type("Hello World!"))
string
> print(type("10.4*3"))
string
> print(type(10.4*3))
number
> print(type(print))
function
> print(type(type))
function
> print(type(true))
boolean
> print(type(nil))
nil
> print(type(type(x)))
string
> print(type(x))
nil
```

### nil 类型

nil 表示是一个无效的值。例如输出一个没有赋值的全局变量，便会得到一个 nil 值。
对于全局变量、table， nil 还有一个作用，给全局变量或者table表里的变量赋值为nil时，等同于把他们删掉。

```lua
> tab1 = { key1 = "val1", key2 = "val2", "val3" } -- 定义表
> for k, v in pairs(tab1) do
>>     print(k .. " - " .. v)
>> end
-- 遍历输出表元素
1 - val3
key1 - val1
key2 - val2
>  
-- 将表的元素置为nil
> tab1.key1 = nil
> for k, v in pairs(tab1) do
>>     print(k .. " - " .. v)
>> end
-- 再次遍历表，发现已经删除了一个元素
1 - val3
key2 - val2
> 

```


### boolean 类型

boolean 类型只有两个可选值： true、false； 
Lua 把false、nil看作是“假”，其他都是“真”。

```lua
# 查看boolean.lua里面的脚本内容
cat boolean.lua 
if false or nil then
    print("至少有一个是 true")
else
    print("false 和 nil 都为 false!")
end

lua boolean.lua -- 执行脚本文件
false 和 nil 都为 false!  -- 输出结果
```

### number 类型

Lua 只有一种number 的双精度类型（默认类型可以修改luaconf.h里的定义）。

```lua
print(type(2))
print(type(2.2))
print(type(0.2))
print(type(2e+1))
print(type(0.2e-1))
print(type(7.8263692594256e-06))

-- 运行结果：
number
number
number
number
number
number
```

### string 字符串

字符串由一堆双引号或单引号来表示。

```lua
string1 = "this is string1"
string2 = 'this is string2'
```

也可以用2个方括号“[[]]”来表示"一块" 字符串。

```lua
html = [[
<html>
<head></head>
<body>
    <a href="http://www.baidu.com/">回到百度</a>
</body>
</html>
]]

print(html)
-- 输出结果为：
<html>
<head></head>
<body>
    a href="http://www.baidu.com/">回到百度</a>
</body>
</html>
```

**注意：** 当尝试对一个数字字符串上进行算术操作时，Lua 会尝试将这个数字字符串转换成一个数字。

```lua
print("123.4" + 4)
127.4
```

**注意：** 字符串连接符号为： **..**

```lua
print("a" .. 'b')
ab

print("a" .. 456)
a456
```

**注意：** 使用 **#** 符号来计算字符串的长度，放在字符串的前面。

 ```lua
 > str = "Hello,girl!"
 > print(#str)
 11
 ```

### table 表

在 Lua里， table的创建是通过“构造表达式”来完成的，最简单的构造表达式是{}，用来创建一个空表。也可以在表里添加一些数据，直接初始化表。

```lua
> tab1 = {}
> print(type(tab1))
table
> tab2 = {"apple", "orange"}
> print(type(tab2))
table
```

Lua 中的表是一个关联数组，数组的索引可以是一个数字或者字符串。

```lua
> a = {}
> a["kel1"] = "val1"
> a[1] = 1

> print(a[1])
1
> print(a["kel1"])
val1
```

**注意：** 和其他语言不一样，Lua表的默认初始索引值一般从1开始。

### function 函数

在Lua中，函数被看作是“第一类值（first-CLass Value）”，函数可以存在变量里。使用 **function** 关键字定义。

创建一个文件，写入：

```shell
echo '' > func.lua

vim func.lua 
## 写入以下内容

function factorial1(n)
    if n == 0 then
        return 1
    else
        return n * factorial1(n - 1)
    end
end
print(factorial1(5))
factorial2 = factorial1
print(factorial2(5))

# 写入完毕
ESC键，输入:符号，wq! 保存退出
```

执行脚本文件: 
```lua
lua func.lua 
120
120
```

我们定义了一个函数，并调用它。 
然后将函数factorial1赋值给另一个变量factorial2，再调用另外一个变量factorial2（函数）。

function 还可以以匿名函数的方式通过参数传递：

```lua
-- function_test2.lua 脚本文件
function testFun(tab,fun)
    for k ,v in pairs(tab) do
        print(fun(k,v));
    end
end


tab={key1="val1",key2="val2"};
testFun(tab,
function(key,val)--匿名函数
    return key.."="..val;
end
);
```

执行脚本:

```lua
lua function_test2.lua 
key1 = val1
key2 = val2
```

### thread 线程

再Lua里，最主要的是协程（coroutine）。它跟线程差不多，拥有自己独立的栈、局部变量和指令指针，可以跟其它协程共享全局变量和其它大部分东西。


## Lua 变量

Lua变量有三种类型： 全局变量、局部变量、表中的域。
Lua中的变量全是全局变量，哪怕是语句块或是函数里，除非用local显式声明为局部变量。
局部变量的作用域为从声明位置开始到所在语句块结束。
变量的值默认为nil。

```lua
-- test.lua 文件脚本
a = 5               -- 全局变量
local b = 5         -- 局部变量

function joke()
    c = 5           -- 全局变量
    local d = 6     -- 局部变量
end

joke()
print(c,d)          --> 5 nil

do 
    local a = 6     -- 局部变量
    b = 6           -- 对局部变量重新赋值
    print(a,b);     --> 6 6
end

print(a,b)      --> 5 6
```

执行lua test.lua 输出结果为:

```lua
lua test.lua

```


### 赋值语句

Lua 可以对多个变量同时赋值，变量列表和值列表的各个元素用逗号隔开，赋值语句右边的值会一次赋给左边的变量。

```lua
a, n = 10, 2*6
```

遇到赋值语句Lua会先计算右边所有的值然后再执行赋值操作，所以可以这样交换变量的值：

```lua
x, y = y, x  -- 直接交换了x，y二者的值；和python一样
a[i], a[j] = a[j], a[i] -- 直接交换二者
```

当变量个数和值个数不匹配时，Lua以变量个数为基础，值不够的默认为nil；值过多的忽略。


Lua支持函数返回多个值，可以采用多值赋值：
```lua
> function multival()
>>     return 1, 2
>> end
> f, g = multival()
> print(f, g)
1       2
```

**总结：** 应该尽可能使用局部变量，有两个好处：
- 避免命名冲突
- 访问局部变量速度比全局变量更快


### 索引 

对table的索引使用方括号 **[]**。Lua 也提供了 **.** 操作。

```lua
t[i]
t.i                 -- 当索引为字符串类型时的一种简化写法
gettable_event(t,i) -- 采用索引访问本质上是一个类似这样的函数调用
```

```lua
> arr = {}
> arr["name"] = 'Mary'
> print(arr["name"])
Mary
> print(arr.name)
Mary
```







