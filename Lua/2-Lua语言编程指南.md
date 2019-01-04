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

for i, n in square(3, 0) do
    print(i, n)
end
```






