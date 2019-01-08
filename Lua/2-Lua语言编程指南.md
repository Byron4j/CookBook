# Lua 语言编程指南


## Lua 循环

Lua 提供了以下几种循环处理方式：

|循环类型|描述|
|----|----|
|while循环|在条件为 true 时，让程序重复地执行某些语句。执行语句前会先检查条件是否为 true。|
|for循环|重复执行指定语句，重复次数可在 for 语句中控制|
|repeat...until|重复执行循环，直到 指定的条件为真时为止|
|循环嵌套|可以在循环内嵌套一个或多个循环语句（while do ... end;for ... do ... end;repeat ... until;）|

### while 循环

语法：
```lua
while(condition)
do
   statements
end
```

示例：

```lua
a=10
while( a < 20 )
do
   print("a 的值为:", a)
   a = a+1
end

输出：
a 的值为:    10
a 的值为:    11
a 的值为:    12
a 的值为:    13
a 的值为:    14
a 的值为:    15
a 的值为:    16
a 的值为:    17
a 的值为:    18
a 的值为:    19
```

### for 循环

for循环分为两大类：
- 数值for循环
- 泛型for循环

#### 数值for循环

语法格式：

```lua
for var=exp1,exp2,exp3 do
    <语句块>
end
```

var 从 exp1 变化到 exp2，每次变化以exp3为步长递增var，并执行一次语句块。exp3可选，不指定则默认为1.
示例：

```lua
> for i = 1, 10, 1 do
>> print(i)
>> end
1
2
3
4
5
6
7
8
9
10
```


```lua
for i=1,f(x) do
    print(i)
end
 
for i=10,1,-1 do
    print(i)
end
```
**注意：** for的三个表达式在循环开始前一次性求值，以后不再进行求值。比如上面的f(x)只会在循环开始前执行一次，其结果用在后面的循环中

#### 泛型for循环

泛型for循环通过一个迭代器函数来遍历所有的值，类似java中的foreach语句。
语法格式：

```lua
a = {"one", "two", "three"}
for i, v in ipairs(a) do
    print(i, v)
end
```

i 是数组索引值， v是对应索引的数组元素值。 **ipairs** 是 Lua 提供的一个迭代器函数，用来迭代数组。

示例：

```lua
> arr = {"Lily", "Jim", "Lucy", "Kate"}
> for i, v in ipairs(arr) do
>>     print("arr[" .. i .. "]=" .. v)
>> end
arr[1]=Lily
arr[2]=Jim
arr[3]=Lucy
arr[4]=Kate
```

### repeat...until 循环

repeat...until条件语句在当前循环结束后进行判断。

语法格式：
```lua
repeat
    statements
until(condition)
```

示例：

```lua
a = 10

repeat
    print("a的当前值", a)
    a = a + 1
until(a > 15)
```
输出a的值，直到a的值大于15的时候，运行结果如下：

```lua
> a = 10
> 
> repeat
>>     print("a的当前值", a)
>>     a = a + 1
>> until(a > 15)
a的当前值       10
a的当前值       11
a的当前值       12
a的当前值       13
a的当前值       14
a的当前值       15
```


## Lua 流程控制语句

**注意**：Lua中除了false、nil为“假”之外其他所有值全部为“真”，0也是“真”。

|语句|描述|
|----|----|
|if语句|if 语句 由一个布尔表达式作为条件判断，其后紧跟其他语句组成。|
|if...else语句|if 语句 可以与 else 语句搭配使用, 在 if 条件表达式为 false 时执行 else 语句代码|
|if嵌套语句|可以在if 或 else if中使用一个或多个 if 或 else if 语句 。|


### if 语句

语法格式：
```lua
if(布尔表达式)
then
  -- 语句块
end
```

示例：
```lua
a = 10

if (a < 20)
then
    print("值小于20")
end
print("a的值为:", a)
```

### if...else 语句

语法格式：

```lua
if (布尔表达式)
then
    --语句块
else
    --语句块
end
```

示例：
```lua
score = 90
if(score >= 90)
then 
    print("优秀")
else
    print("一般")
end
# 输出：
优秀
```

### if...elseif...else 语句

示例：
```lua
--[ 定义变量 --]
a = 100

--[ 检查布尔条件 --]
if( a == 10 )
then
   --[ 如果条件为 true 打印以下信息 --]
   print("a 的值为 10" )
elseif( a == 20 )
then   
   --[ if else if 条件为 true 时打印以下信息 --]
   print("a 的值为 20" )
elseif( a == 30 )
then
   --[ if else if condition 条件为 true 时打印以下信息 --]
   print("a 的值为 30" )
else
   --[ 以上条件语句没有一个为 true 时打印以下信息 --]
   print("没有匹配 a 的值" )
end
print("a 的真实值为: ", a )
```

这是一个给考试分数判定级别的分支程序：

```lua
> score = 85
> if(score>90)
>> then
>>     print("优秀,分数:", score)
>> elseif(score>=80)
>> then
>>     print("良好,分数:", score)
>> elseif(score>=70)
>> then
>>     print("一般,分数:", score)
>> elseif(score>=60)
>> then 
>>     print("合格,分数:", score)
>> else
>>     print("较差,分数:", score)
>> end
良好,分数:      85
```

### if嵌套语句

语法格式类似：
```lua
if( 布尔表达式 1)
then
   --[ 布尔表达式 1 为 true 时执行该语句块 --]
   if(布尔表达式 2)
   then
      --[ 布尔表达式 2 为 true 时执行该语句块 --]
   end
end
```

## Lua 函数

函数定义语法格式：

```lua
optional_function_scope function function_name( argument1, argument2, argument3..., argumentn)
    function_body
    return result_params_comma_separated
end
```

- optional_function_scope: 可选的指定函数是全局函数还是局部函数，默认为全局的； 局部使用 **local** 修饰。
- function_name： 指定函数名称。
- argument1, argument2, argument3..., argumentn：函数参数列表，逗号隔开，也可以不带参数。
- function_body： 函数体
- result_params_comma_separated：函数返回值，Lua语言函数可以返回多个值，用逗号隔开

示例：

```lua
function max(num1, num2)
    if(num1 > num2) then
        return num1
    else
        return num2
    end
end

print("比较二者的最大数：", max(1, 4))  
print("比较二者的最大数：", max(20, 3))
```

Lua 中可以将函数作为参数传递给函数，示例：

```lua
myprint = function(param)
    print("这是我的打印函数，打印param:", param)
    end

function add(num1, num2, func4Print)
     func4Print(num1 + num2)
end 


add(2, 3, myprint)   
- 输出：
这是我的打印函数，打印param:    5
```

### 多返回值函数

Lua 函数可以返回多个值，比如 string.find，其返回匹配串“开始和结束的下标”（如果不存在匹配串则返回nil）

```lua
> print(string.find("www.baidu.com", "baidu"))
5       9
```

示例：

```lua
function maximum (a)
    local mi = 1             -- 最大值索引
    local m = a[mi]          -- 最大值
    for i,val in ipairs(a) do
       if val > m then
           mi = i
           m = val
       end
    end
    return m, mi
end

print(maximum({8,10,23,12,5}))
-- 输出 
23    3
```

### 可变参数函数

Lua 函数支持可变参数。 和C语言类似，在参数列表中使用三点符 **...** 表示函数有可变的参数

示例：

```lua
function add(...) --可变参数
local s = 0
    for i, v in ipairs{...} do -- {...}表示一个可变长参数构成的数组
        s = s + v
    end
    return s
end

print(add(3,4,5,6,7))
-- 输出结果为：
25
```

**select("#", ...)** 表达式来获取可变参数的个数

```lua
function average(...)
   result = 0
   local arg={...}
   for i,v in ipairs(arg) do
      result = result + v
   end
   print("总共传入 " .. select("#",...) .. " 个数")
   return result/select("#",...)
end

print("平均值为",average(10,5,3,4,5,6))

-- 输出结果为：
总共传入 6 个数
平均值为        5.5
```

**注意**： 有时候需要几个固定参数 + 可变参数， 固定参数必须放在可变参数之前。

```lua
function fwrite(fmt, ...)
    return io.write(string.format(fmt, ...))
end

fwrite("helloboy\n")
-- 输出
helloboy
```


- select("#", ...) : 返回可变参数的长度
- select(n, ...) : 用于访问 n 到 select("#", ...) 的参数

```lua
function foo(...)
    for i = 1, select('#', ...) do
        local arg = select(i, ...)
        print("arg", arg) 
    end
end

foo(1, 2, 3, 4)

-- 输出结果为：
arg     1
arg     2
arg     3
arg     4
```

## Lua 运算符

Lua 提供了以下几种运算符：

- 算术运算符
- 关系运算符
- 逻辑运算符
- 其他运算符

### 算术运算符

+、-、*、/、%、^（乘幂，例如 2^2 值为 4）、-（负号）。

### 关系运算符

|操作符|描述|示例|
|----|----|----|
|==|等于|1==1 为true|
|**~=**|不等于|1~=1为false|
|>|大于|2>1为true|
|<|小于|1<2为true|
|>=|大于等于|1>=1为true|
|<=|小于等于|1<=2为true|

### 逻辑运算符
逻辑运算符共有三个，示例，假设A为true，B为false

- and ： A and B 为false
- or ： A or B 为true
- not ： not(A and B) 为true

###  其他运算符

|操作符|描述|示例|
|----|----|----|
|**..**|连接两个字符串|a = "Hello", b = "World!", a..b = "HelloWorld!"|
|**#**|返回字符串或表的长度|#"Hello" 返回5|


```lua
> a = "Hello"
> b = ",world!"

> print(a..b)
Hello,world!

> print(#a)
5
```

运算符优先级：
除了^和..外所有的二元运算符都是左连接的。从高到低的顺序依次是：
```
^
not    - (unary)
*      /
+      -
..
<      >      <=     >=     ~=     ==
and
or
```

## Lua 字符串

Lua 中的字符串可以使用以下三种方式来表示：

- 单引号之间的一串字符
- 双引号之间的一串字符
- [[ 和 ]] 之间的一串字符

示例如下：

```lua
> str = [["你好"]]
> print(str)
"你好"
> str = [[你好]]
> print(str)
你好
```

### 字符串操作函数

字符串string提供了一系列静态方法
|序号|函数|描述|
|----|----|----|
|1|string.upper(argument)|字符串全部转换为大写字母|
|2|string.lower(argument)|字符串全部转换为小写|
|3|string.gsub(main String, find String, replaceString, num)|在字符串中替换，mianString为与奥替换的字符串，findString为被替换的字符，replaceString为要替换的字符，num替换次数（不指定则全部替换）|
|4|string.find(str, substr, [init,[end]])|在一个制定的目标字符串中搜索指定的内容（第三个参数为索引）。返回其具体位置，不存在则返回nil|
|5|string.reverse(arg)|字符串反转|
|6|string.format(...)|返回一个类似print的格式化字符串|
|7|string.char(arg)和string.byte(arg[,int])|char将整型字转换成字符并连接，byte转换字符为整数值（可以指定某个字符，默认第一个字符）|
|8|string.length(arg)|计算字符串长度|
|9|string.rep(string, n)|返回字符串string的n个拷贝|
|10|..|连接两个字符|
|11|string.gmatch(str, pattern)|一个迭代器函数，每一次调用这个函数，返回一个在字符串str找到的下一个符合pattern描述的字串。如果参数pattern描述的字符串没有找到，迭代函数返回nil|
|12|string.match(str, pattern, init)|只寻找源字串str中的第一个配对，参数init可选，指定搜寻过程的起点，默认为1.在成功配对时，函数将返回配对表达式中的所有捕获结果，如果没有设置捕获标记，则返回整个配对字符串，当没有成功的配对时，返回nil。|


## Luau数组

数组，是相同数据类型的元素按照一定顺序排列的集合，可以是一维数组和多维数组
Lua 数组的索引值可以使用整数表示，数组的大小不是固定的。

### 一维数组

是最简单的数组，逻辑是线性表。 一维数组可以用for循环出数组中的元素。 如下示例：

```lua
array = {"Lua", "Hello"}

for i=0, 2 do
    print(array[i])
end

-- 输出结果为：
nil
Lua
Hello
```

在 Lua 中索引值是从 1开始的， 如果指定的索引没有值则返回nil。还可以是负值。

### 多维数组

```lua
-- 初始化数组
array = {}
for i=1,3 do
   array[i] = {}
      for j=1,3 do
         array[i][j] = i*j
      end
end

-- 访问数组
for i=1,3 do
   for j=1,3 do
      print(array[i][j])
   end
end
```


## 迭代器

迭代器是一种对象，能够用来遍历标准模板库容器中的部分或全部元素，每个迭代器对象代表容器中的确定的地址。
在Lua中迭代器一种支持指针类型的结构，可以遍历集合的每一个元素。

### 泛型 for 迭代器

泛型 for 在自己内部保存迭代器，实际上它保存三个值： 迭代函数、状态常量、控制变量。
泛型 for 迭代器提供了集合的 key/value 对， 语法格式如下：

```lua
for k, v in ipairs(t) do
    print(k, v)
end
```

示例：

```lua
> arr = {"Lua", "Hello"}
> for i, v in ipairs(arr) do
>>     print(i, v)
>> end
1       Lua
2       Hello
```

在Lua中我们常常使用函数来描述迭代器，每次调用该函数就返回集合的下一个元素。Lua的迭代器包含以下两种类型：

- 无状态的迭代器
- 多状态的迭代器

### 无状态的迭代器

无状态的迭代器是指不保留任何状态的迭代器，因此在循环中我们可以利用无状态迭代器避免创建闭包花费额外的代价。
每一次迭代，迭代函数都是用两个变量（状态控制 和 控制变量）的值作为参数被调用，一个无状态的迭代器只利用这两个值可以获取下一个元素。

这种无状态的迭代器的典型的简单的例子是 ipairs，它遍历数组的每一个元素。
一个简单的迭代器实现数字n的平方。

```lua
function square(iteratorMaxCount, currentNumber)
    if currentNumber < iteratorMaxCount
    then
        currentNumber = currentNumber + 1
    return currentNumber, currentNumber*currentNumber
    end
end

for i,n in square,3,0
do
   print(i,n)
end

-- 输出：
1    1
2    4
3    9
```

迭代的状态包括被遍历的表（循环过程中不会改变的状态常量）和当前的索引下标（控制变量），ipairs和迭代函数都很简单，还可以这样实现：
```lua
function iter (a, i)
    i = i + 1
    local v = a[i]
    if v then
       return i, v
    end
end
 
function ipairs (a)
    return iter, a, 0
end
```

当Lua调用ipairs(a)开始循环的时候，它获取三个值：迭代函数iter、状态常量a、控制变量初始值0；
然后Lua调用iter(a,0)返回1,a[1](除非a[1]为nil);
第二次迭代调用 iter(a, 1)，返回2,a[2]......直到第一个nil元素。

### 多状态的迭代器

很多情况下，迭代器需要村村多个状态信息而不是简单的状态常量和控制变量，最简单的方法是使用闭包。
还有一种方法是将所有的状态信息封装到table中，将table作为迭代器的状态常量，因为这种情况下将所有的信息存放在table内，
所以迭代函数通常不需要第二个参数。

```lua
function iterator(collection)
    local index = 0
    local count = #collection
    --闭包函数
    return function()
        index = index + 1
        if index <= count
        then
            -- 返回迭代器的当前元素
            return collection[index]
        end
    end
end

array = {"Lua", "Hello"}

for ele in iterator(array)
do
    print(ele)
end
```

## Lua 表 Tbale

table 是 Lua的一种数据结构，以帮助我们创建不同的数据类型，如：数组、字典等。
Lua table 使用关联数组，可以用任意类型的值作为数组的索引，只要索引值不是nil。
Lua table 是不固定大小的，剋根据自己需要进行扩充。
Lua 也是通过table来解决模块、包和对象的。
例如 string.format 表示使用"format"来索引table string。

### table 的构造

构造器是创建和初始化表的表达式。最简单的构造是{}，用以构建一个空表。

```lua
myTb = {}

-- 指定值构造
myTb[1] = "Hello"

-- 移除引用，Lua 垃圾回收会释放内存
myTb = nul

```


## Lua 模块与包

模块类似于一个封装库，从Lua5.1开始，Lua加入了标准的模块管理机制，可以把一些公用的代码放在一个文件里。以API接口的形式放在其他地方调用，有利于代码的重用和降低代码耦合度。
Lua模块是由变量、函数等已知元素组成的 table，因此创建一个模块很简单，就是创建一个table。
自定义一个模块，格式如下：

```lua
-- 文件名为 module.lua
-- 定义一个名为 module 的模块
module = {}

-- 定义一个常量
module.constant = "这是一个常量"

-- 定义一个函数
function mudule.func1()
    io.write("这是一个公有函数!\n")
end


local function func2()
    print("这是一个私有函数!")
end


function module.func3()
    func2()
end

```

func2 声明为程序块的局部变量，表示是一个私有函数，不能从外部直接访问。


### 模块引入函数 require

Lua 提供了一个名为 require 的函数来加载模块。要加载一个模块，只需要简单调用就可以了。例如：
```lua
require("模块名")

-- 或者

require "模块名"
```

**注意**:执行 require 后会返回一个由模块常量或函数组成的table，并且还会定义一个包含该 table 的全局变量。

```lua
--当前是 test_module.lua 文件
-- module 模块是前面提到的 module.lua
require("module")

-- 调用引入模块的常量
print(module.constant)

-- 调用模块内定义的公有函数
module.func3()
```

### 加载机制

对于自定义的模块，模块文件不是放在那个文件目录都可以的。
函数 require 有它自己的文件路径加载策略，会尝试从 Lua 文件 或 C 程序库中加载模块。

require 用于搜索 Lua 文件的路径是存放在全局变量 package.path 中，当 Lua 启动后，会以环境变量 **LUA_PATH** 的值来初始化这个环境变量。
如果没有找到该环境变量，则使用一个编译时定义的默认路径来初始化。

## Lua 元表

在 Lua table 中我们可以访问对应的 key 来得到 value 的值，但是却无法对两个 table 进行操作。
因此 Lua 提供了元表（Metatable），允许我们改变table的行为，每个行为关联了对应的元方法。
例如： 使用 元表我们可以定义 Lua 如何计算两个 table 的相加操作 a+b。
当 Lua 试图对两个表进行相加时，先检查两者之一是否有元素，之后检查是否有一个叫 "__add" 的字段，有则调用对应的值。

有2个很重要的函数来处理元素：
- setmetatable(table, metatable): 对指定 table 设置元表（metatable），如果元表（metatable）中存在  __metatable 键值，setmetatable 会失败。
- getmetatable(table): 返回对象的元素（metatable）

示例：
```lua
mytable = {}                         -- 普通表
mymetatable = {}                     -- 元表
setmetatable(mytable, mymetatable)   -- 把 mymetatable 设为 mytable 的元素
```

以上代码也可以直接写成一行：
```lua
mytable = setmetatable({}, {})

-- 返回 mymetatable
getmetatable(mytable)
```

### __index 元方法

#### __index 包含表table示例

这是 metatable 最常用的键。
当你通过键来访问 table 的时候，如果这个键没有值，那么Lua就会寻找该table的metatable(假设有metatable)中的__index键。
如果 __index 包含一个表格，Lua 会在表格中查找相应的键。

```lua
other = { foo = 3 }
t = setmetatable({}, { __index = other })
t.foo
-- 值为3
```

解析：通过键foo访问table，就寻找表t的元表的__index键,存在 __index 包含一个表格other，则在other表中查找对应的foo键，找到值为3.

#### __index 包含函数示例

**如果 __index 包含一个函数的话，Lua 就会调用那个函数， table 和 键会作为参数传递给函数。**
__index 元方法查看表中元素是否存在，如果不存在，返回结果为 nil；如果存在则由 __index 返回结果。

```lua
mytable = setmetatable({key1 = "value1"}, {
    __index = function(mytable, key)
                    if key == "key2" then
                        return "metatablevalue"
                    else
                        return nil
                    end
                end
})

print(mytable.key1, mytable.key2, mytable.key3)
-- 输出结果为：
val1    val2    nil

```

**给表 mytable 亦即 {key1 = "value1"}，设置了元方法 __index 指向一个函数，如果传入key为key2的话，返回value2，这样类似增加了 key2=value2，
使得 mytable = {key1 = "value1", key2 = "value2"}**

解析如下：
- mytable 赋值为 **{key1="value1"}**
- mytable 设置了元素，元方法为 __index
- 在 mytable 中查找 key1，如果找到，返回该元素，找不到则继续
- 在 mytable 中查找 key2，如果找到，返回 metatablevalue，找不到则继续
- 判断表有没有  __index方法，如果 __index方法是一个函数，则调用该函数
- 元方法中查看是否传入了 "key2" 键的参数(mytable.key2已设置)，如果传入"key2" 参数返回"metatablevalue",否则返回mytable对应的键值。

上述使用函数的可以改写为使用表table：
```lua
mytable = setmetatable({key1="key1"}, __index = {key2="metatablevalue"})

print(mytable.key1, mytable.key2)
```

**总结**

Lua 查找一个表元素时的规则，其实就是如下三个步骤：

- 在表中查找，如果找到，返回该元素，找不到则继续。
- 判断该表是否有元素，如果没有元素，返回nil，有元素则继续。
- 判断元素有没有 __index 方法， 如果 __index 方法为nil， 则返回nil；如果 __index 方法是一个表，则重复1、2、3；如果 __index 方法是一个函数，则返回该函数的返回值。

### __newindex 元方法

**__newindex 元方法用来对表更新， __index 则用来对表访问。**
当你给表的一个缺少的索引赋值，解释器就会查找 __newindex 元方法；如果存在则调用这个函数而不进行赋值操作。
示例：

```lua
mymetatable = {}
mytable = setmetatable({key1="value1"}, {__newindex=mymetatable})

print(mytable.key1)

> mytable.newkey = "新值2"
> print(mytable.newkey, mymetatable.newkey)
nil     新值2
> mytable.key1 = "新值1"
> print(mytable.key1, mymetatable.key1)
```

上述示例：给表设置了元方法 __newindex，在对新索引键（newkey）赋值时（mytable.newkey="新值2"），会调用元方法，而不进行赋值。
而如果对已存在的索引键（key1），则会进行赋值，而不调用元方法 __newindex。

### 给表添加操作符

演示为2个表添加相加操作：

```lua
-- 计算表中最大值， table.maxn 在 Lua5.2以上版本中已无法使用
-- 自定义计算表中最大键值函数 table_maxen,即计算表的元素个数

function table_maxn(t)
    local mn = 0
    for k, v in pairs(t) do
        if mn < k then
            mn = k
        end
    end
    return mn
end

-- 两表相加操作
mytable = setmetatable({1, 2, 3}, {
            __add = function(mytable, newtable)
                        for i = 1, table_maxn(newtable) do
                                table.insert(mytable, table_maxn(mytable)+1, newtable[i])
                        end
                     return mytable
                   end
})

secondtable = {4,5,6}

mytable = mytable + secondtable
for k, v in ipairs(mytable) do
    print(k, v)
end


```

__add 键包含在元素中，并进行相加操作。表中对应的操作列表如下：

|模式|描述|
|----|----|
|__add|对应的运算符'+'|
|__sub|对应的运算符'-'|
|__mul|对应的运算符'*'|
|__div|对应的操作符'/'|
|__mod|对应运算符'%'|
|__concat|对应连接符'...'|
|__eq|对应'=='|
|__lt|对应'<'|
|__le|对应'<='|
|__unm|对应负号'-'|

### __call 元方法

__call 元方法在Lua调用一个值时调用。
演示计算表中元素的和：
```lua
function table_maxn(t)
    local mn = 0
    for k, v in pairs(t) do
        if mn < k then
            mn = k
        end
    end
    return mn
end

-- 定义元方法__call
mytable = setmetatable({10}, {
  __call = function(mytable, newtable)
    sum = 0
    for i = 1, table_maxn(mytable) do
        sum = sum + mytable[i]
    end
    for i = 1, table_maxn(newtable) do
        sum = sum + newtable[i]
    end
    return sum
  end
})

newtable = {10,20,30}
print(mytable(newtable))
-- 输出：
70

```

### __tostring 元方法

__tostring 元方法用于修改表的输出行为。
```lua
mytable = setmetatable({10, 20, 30}, {
            __tostring = function(mytable)
                            sum = 0
                            for k, v in pairs(mytable) do
                                sum = sum + v
                            end
                            return "表所有元素之和为:" ... sum
                        end
})

-- 直接打印表，则调用其tostring方法
print(mytable)
-- 输出：
表所有元素的和为 60
```

**总结**： 元表可以很好的简化代码功能，了解元表，可以写出更加简洁优秀的Lua代码。

