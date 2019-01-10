# Lua 编程高级

## Lua 协同程序(coroutine)

### 什么是协同(coroutine)?

Lua 协同程序coroutine与线程比较类似：拥有独立的堆栈、独立的局部变量，独立的指令指针，同时又与其他协同程序共享全局变量和其他大部分资源。
协同是非常强大的功能，但是用起来也很复杂。

#### 线程和协同程序的区别

线程与协同程序的主要区别在于，一个具有多个线程的程序可以同时运行几个线程，而协同程序却需要彼此协作的运行。
在任意时刻只有一个协同程序在运行，并且这个正在运行的程序只有在摩纳哥却的被要求挂起的时候才会被挂起。
协同程序有点类似同步的多线程，在等待同一个线程锁的几个线程类似协同程序。

#### 语法

|方法|描述|
|----|----|
|coroutine.create()|创建coroutine，参数是一个函数，当和resume配合使用的时候就唤醒函数调用|
|coroutine.resume()|重启coroutine，和create配合使用|
|coroutine.yield()|挂起coroutine，将coroutine设置为挂起状态，这个和resume配合使用可以产生很多效果|
|coroutine.status()|查看coroutine的状态：dead,suspend,running|
|coroutine.wrap()|创建coroutine，返回一个函数，一旦你调用这个函数，就进入coroutine，和create功能重复|
|coroutine.sunning()|返回正在跑的coroutine，一个coroutine就是一个线程，当使用sunning的时候，就是返回一个coroutine的线程号|

#### 示例

```lua
-- coroutine_test.lua 文件
co = coroutine.create(
        function(i)
            print(i)
        end
)

print(coroutine.status(co)) --susoend
coroutine.resume(co, 1)     -- 1
print(coroutine.status(co))  -- dead

co = coroutine.wrap(
    function(i)
        print(i);
    end
)
 
co(1)  -- 1

> coroutine.resume(co2) 
1
> coroutine.resume(co2) 
2
> coroutine.resume(co2) 
3
running
thread: 0x6a40c0
> print(coroutine.status(co2))
suspended
```
从coroutine.running()的输出可以看出，coroutine在底层实现就是一个线程。

当create一个coroutine的时候就是在新线程中注册了一个事件。
当使用resume触发事件的时候，create的coroutine函数就被执行了，当遇到yield的时候就代表挂起当前线程，等候再次resume触发事件。
示例：

```lua
function foo (a)
    print("foo 函数输出", a)
    return coroutine.yield(2 * a) -- 返回  2*a 的值
end
 
co = coroutine.create(function (a , b)
    print("第一次协同程序执行输出", a, b) -- co-body 1 10
    local r = foo(a + 1)
     
    print("第二次协同程序执行输出", r)
    local r, s = coroutine.yield(a + b, a - b)  -- a，b的值为第一次调用协同程序时传入
     
    print("第三次协同程序执行输出", r, s)
    return b, "结束协同程序"                   -- b的值为第二次调用协同程序时传入
end)

print("main", coroutine.resume(co, 1, 10)) 
-- 传入a，b分别值分别为1，10；调用foo函数传入参数为2，协同程序在foo里面调用yield挂起，返回4.
print("--分割线----")
print("main", coroutine.resume(co, "r")) -- true 11 -9
-- 第二次重启协同程序传入字符串"r"参数，挂起传入参为 1+10; 1-10
print("---分割线---")
print("main", coroutine.resume(co, "x", "y")) -- true 10 end
-- 第三次重启，输出第三次执行输出的内容。结束协同程序。
print("---分割线---")
print("main", coroutine.resume(co, "x", "y")) -- cannot resume dead coroutine
-- 协同程序已经终止，无法再次启动
print("---分割线---")

--输出：
第一次协同程序执行输出    1    10
foo 函数输出    2
main    true    4
--分割线----
第二次协同程序执行输出    r
main    true    11    -9
---分割线---
第三次协同程序执行输出    x    y
main    true    10    结束协同程序
---分割线---
main    false    cannot resume dead coroutine
---分割线---

```

- 调用resume，将协同程序唤醒，resume操作成功返回true，否则返回false。
- 协同程序运行
- 运行到yield语句
- yield挂起协同程序，第一次resume返回；（注意：此处yield返回，参数是resume的参数）
- 第二次resume，再次唤醒协同程序；（注意：此处resume参数中，除了第一个参数，剩下的参数作为yield的参数）
- yield返回
- 协同程序继续运行
- 如果使用的协同程序继续运行完成后继续调用resume方法则输出：cannot resume dead coroutine

resume 和 yield 的配合强大之处在于， resume处于主程中，它将外部状态（数据）传入到协同程序内部；而 yield 则将内部的状态（数据）返回到主程中。


### 生产者-消费者问题

使用 Lua 协同程序来完成 生产者-消费者 模式。

```lua
local new Productor

- 定义生产者
function productor()
    local i = 0
    while true do
        i = i + 1
        send(i)  -- 将生产的消息发送给消费者
    end
end

-- 定义消费者
function consumer()
    while true do
        local i = receive()
        print(i)
    end
end

function receive()
    local status, value = coroutine.resume(newProductor)
    return value
end

function send(x)
    coroutine.yield(x)  --发送x，值返回后，挂起该协同程序
end

-- 启动程序
newProductor = coroutine.create(productor) -- 使用生产函数创建一个协同程序
consumer() --启动消费

```


## Lua 文件 IO

Lua I/O 库用于读写文件。 分为简单模式(和C一样)、完全模式。

- 简单模式： 拥有一个当前输入文件和一个当前输出文件，并且提供针对这些文件相关的操作。
- 完全模式： 使用外部的文件句柄来实现。他以一种面向对象的形式，将所有的文件操作定义为文件句柄的方法。

简单模式在做一些简单的文件操作时较为合适。但是在进行一些高级的文件操作的时候，简单模式就显得力不从心。
例如同时读取多个文件这样的操作，使用完全模式较为合适。

打开文件操作语句如下：
```lua
file = io.open(filename [, mode])
```

mode 的值有：

|模式|描述|
|----|----|
|r|以只读方式打开文件，该文件必须存在|
|w|打开只写文件，若文件存在则文件长度清为0，即该文件内容会消失。若文件不存在则建立该文件|
|a|以附加的方式打开只写文件。若文件不存在则建立该文件，如果文件存在，则写入的数据会被追加到文件末尾，即文件原来的内容会被保留。（EOF保留）|
|r+|以可读写方式打开文件，该文件必须存在|
|w+|打开可读写文件，若文件存在则清零，不存在则建立该文件|
|a+|与a类似，但此文件可读可写|
|b|二进制模式，如果文件是二进制文件，可以加上b|
|+|表示对文件可读也可写|

### 简单模式

简单模式使用标准的 I/O 或使用一个当前输入文件和一个当前输出文件。
创建一个 test.lua 文件。

```lua
-- 以只读方式打开文件
file = io.open("test.lua", "r")

-- 设置默认输入文件为 test.lua; 对文件进行 I/O操作前，需要将其设置为输入文件
io.input(file)

-- 输出文件第一行
print(io.read())

-- 关闭打开的文件
io.close(file)

-- 以附加的方式打开只写文件
file = io.open("test.lua", "a")

-- 设置默认输出文件为 test.lua
io.output(file)

-- 在文件最后一行添加 Lua 注释  -- write会返回写入的内容
io.write("--  test.lua 文件末尾注释")    

-- 关闭打开的文件
io.close(file)


```

输出：
```--  test.lua 文件末尾注释`````

在以上示例使用了 io."x" 方法，其中 io.read() 方法我们没有带参数，参数可以是下表中的一个：

|模式|描述|
|----|----|
|"n"|读取一个数字并返回它。例如 file.read("n")|
|"*a"|从当前位置读取整个文件。例如 file.read("*a")|
|"*l"|默认。读取下一行，在文件末尾（EOF）处返回nil。例如： file.read("*l")|
|number|返回一个指定字符个数的字符串，或者在EOF时返回nil。例如： file.read(5)|

其他的IO方法有：
- **io.tmpfile()** : 返回一个临时文件句柄，该文件以更新模式打开，程序结束时自动删除。
- **io.type(file)** ： 检测io是否一个可用的文件句柄
- **io.flush()** ： 向文件写入缓冲中的所有数据
- **io.lines(optional file name)** ： 返回一个迭代函数，每次调用将获得文件中的一行内容，当到文件尾时，将返回nil，但不关闭文件。


### 完全模式

通常我们需要在同一时间处理多个文件。我们需要使用 file:funcname 来代替 io.funcname 方法。

```lua
file = io.open("test.lua","r")
print(file:read())

file:close()

file = io.open("test.lua", "a")

file:write("--test")

file:close()
```

其他方法：

- file:seek(optional whence, optional offset) : 设置和获取当前文件位置，成功则返回最终的文件位置（按字节），失败则返回nil加错误信息。
参数whence的值可以是：
    - "set" : 从文件头开始
    - "cur" : 从当前位置开始（默认）
    - "end" : 从文件末尾开始
    - "offset" : 默认为0
    
    不带参数 file:seek() 则返回当前位置，file:seek("set")则定为到文件头，file:seek("end")则定为文件末尾并返回文件大小。
 - file:flush() ： 向文件写入缓冲中的所有数据
 - io.lines(optional file name) : 打开指定的文件filename为读模式并返回一个迭代函数，每次调用将获得文件中的一行内容，当到文件末尾时，将返回nil，并自动关闭文件。
  若不带参数时io.lines() <=> io.input():lines(); 读取默认输入设备的内容，但结束时不关闭文件，如：
  
```lua
for line in io.lines("test.lua") do
    print(line)
end
```

以下示例使用了seek方法，定位到文件倒数第25个位置并使用read方法的*a参数，即从当前位置（倒数第25个位置）读取整个文件。

```lua
file = io.open("test.lua","r")

file:seek("end", -25)

print(file:read("*a"))

file:close()
```


## Lua 错误处理机制

程序运行中错误处理是很有必要的，在我们进行文件操作，数据转移以及web service 调用过程中都会出现不可预期的错误。
如果不注重错误信息的处理，就会造成信息泄露，程序无法运行等情况。
任何语言，都需要进行错误处理，错误类型分为：
- 语法错误
- 运行错误

### 语法错误

语法错误通常是由于对程序组件（如运算符、表达式、函数等）使用不当导致的，一个简单示例如下：
```lua
a ==2
```
执行结果为： 
```lua
stdin:1: '=' expected near '=='
```

另一个示例：
```lua
for a= 1,10
   print(a)
end

-- 提示错误如下：
stdin:2: 'do' expected near 'print'
```

语法错误比程序运行错误更简单，语法错误可以很快解决，上述示例提示在print之前需要加上do。

### 运行错误

运行错误是程序可以正常执行，但是会输出报错信息，如下参数输入错误，程序执行时报错：
```lua
function add(a, b)
    return a+b
end

add(10)
```

### 错误处理

Lua 中可以使用两个函数 ： **assert** 和 **error** 来处理错误，如下：
```lua
function add(a, b)
    assert(type(a) == "number", "a 不是一个数字")
    assert(type(b) == "number", "b 不是一个数字")
    return a+b
end

> add(10)
stdin:3: b 不是一个数字
stack traceback:
        [C]: in function 'assert'
        stdin:3: in function 'add'
        stdin:1: in main chunk
        [C]: ?
```

示例中 assert 首先检查第一个参数，没有问题则不做任何事情；
否则检查第二个参数，错误则输出错误信息。

### error 函数

语法格式：
```lua
error (message [, level])

```

功能： 终止正在执行的函数，并返回message的内容作为错误信息（error函数永远都不会返回）
通常情况下，error会附加一些错误位置的信息到message头部。
level参数指示获得错误的位置：
- level = 1(默认)：为调用error位置（文件+行号）
- level = 2：指出哪个调用error的函数的函数
- leve = 0：不添加错误位置信息

```lua
> function add(a, b)
    error("错误信息", b)    
end
> 
> 
> 
> add(b)
stdin:2: 错误信息
stack traceback:
        [C]: in function 'error'
        stdin:2: in function 'add'
        stdin:1: in main chunk
        [C]: ?
```

### pcall 和 xpcall、debug

Lua 中处理错误，可以使用函数pcall（protected call）来包装需要执行的代码。
pcall 接收一个函数和要传递给后者的参数，并执行，执行结果：有错误、无错误；返回值true或者false，errorinfo。

语法格式如下：
```lua
if pcall(funcname, ...) then
    --没有错误
else
    -- 一些错误
end
```

示例如下：
```lua
> = pcall(function(i)
    print(i)  error("错误信息...")
    end, 33)
33
false   stdin:2: 错误信息...
```

pcall 以一种“保护模式”来调用第一个参数，因此pcall可以捕获函数执行中的任何错误。
通常在错误发生时，希望罗德更多的调试信息，而不只是发生错误的位置，但 pcall 返回时，他已经销毁了调用栈的部分内容。

Lua提供了xpcall函数，xpcall接收第二个参数———— 一个错误处理函数，当错误发生的时候，Lua会在调用栈展开(unwind)前调用错误处理函数，于是就可以在这个函数中使用debug库来获取关于错误的额外信息了。
debug库提供了两个通用的错误处理函数：

- debug.debug : 提供一个Lua提示符，让用户来检查错误的原因
- debug.traceback: 根据调用栈来构建一个扩展的错误信息

```lua
> =xpcall(function(i) print(i) error('error..') end, function() print(debug.traceback()) end, 33)
nil
stack traceback:
        stdin:1: in function <stdin:1>
        [C]: in function 'error'
        stdin:1: in function <stdin:1>
        [C]: in function 'xpcall'
        stdin:1: in main chunk
        [C]: ?
false   nil
```

xpcall使用示例1：
```lua
function myfunction ()
   n = n/nil
end

function myerrorhandler( err )
   print( "ERROR:", err )
end

status = xpcall( myfunction, myerrorhandler )
print( status)

-- 输出:
ERROR:  stdin:2: attempt to perform arithmetic on global 'n' (a nil value)
false
```

## Lua 调试

Lua 提供了debug库用于提供创建我们自定义调试器的功能。Lua 本身并没有内置的调试器，但是很多开发者共享了它们的Lua调试器代码。
Lua 中debug库包含以下函数：

|方法|描述|
|----|----|
|debug()|进入一个用户交互模式，运行用户输入的每个字符串。使用简单的命令以及其他调试设置，用户可以检阅全局变量和局部变量，改变变量的值，计算一些表达式，等等。输入一行仅包含cont的字符串将结束这个函数，这样调用者就可以继续向下执行|
|getfenv(object)|返回对象的环境变量|
|gethook(optional thread)|返回三个表示线程钩子设置的值：当前钩子函数、当前钩子掩码、当前钩子计数|
|getinfo([thread,] f [,what])|返回一个函数信息的表。可以直接提供该函数，也可以用一个数字f表示函数。数字f表示运行在指定线程的调用栈对应层次上的函数：0层表示当前函数(getinfo自身)；1表示调用getinfo的函数（除非是尾调用，这种情况下不计入栈）；等等。如果f是一个比活动函数数量还大的数字，getinfo返回nil|
|debug.getlocal([thread,] f, local)|此函数返回在栈的f层处函数的索引为local的局部变量的名字和值。这个函数不仅用于访问显示定义的局部变量，也包括形参、临时变量等。|
|getmetatable(value)|把指定索引指向的值的元素压入堆栈。如果索引无效，或是这个值没有元素，函数将返回0并且不会向栈上压任何东西。|
|getregistry()|返回注册表，这是一个预定义出来的表，可以用来保存任何C代码想保存的Lua值|
|getupvalue(f, up)|此函数返回函数f的第up个上值得名字和值。如果该函数没有那个上值，返回nil。以'('开头得变量名表示没有名字得变量（去除了调试信息得代码块）|
|sethook([thread,] hook, mask [, count])|将一个函数作为钩子函数设入。字符串mask以及数字count决定了钩子在何时调用。掩码是由下列字符组合成得字符串，每个字符有其含义： 'c' : 每当Lua调用一个函数是，调用钩子; 'r' : 每当Lua从一个函数内返回时，调用钩子; 'l' : 每当Lua进入新的一行时，调用钩子|
|seylocal([thread,] level, local, value)|这个函数将value赋给栈上第level层函数的第local个局部变量。如果没有那个变量，函数返回nil。如果level越界，抛出一个错误。|
|setmetatable(value, table)|将value的元素设为table(可以是nil)。返回value|
|setupvalue(f, up, value)|这个函数将value设为函数f的第up个上值，如果函数没有那个上值，返回nil|
|traceback([thread,] [message [, level]])|如果message有，且不是字符串或nil，函数不做任何处理直接返回message。否则，它返回调用栈的栈回溯信息。字符串可选项message被添加在栈回溯信息的开头。数字可选项level指明从栈的哪一层开始回溯（默认为1，即调用traceback的那里）|


简单示例：

```lua
function myfunction()
    print(debug.traceback("堆栈信息："))
    print(debug.getinfo(1))
    print("堆栈信息结束。")
    return 10
end

> myfunction()
堆栈信息：
stack traceback:
        stdin:2: in function 'myfunction'
        stdin:1: in main chunk
        [C]: ?
table: 0x198dab0
堆栈信息结束。
> print(debug.getinfo(1))
table: 0x198e140
```


## Lua 垃圾回收

Lua 采用了自动内存管理。这意味着你不用操心新创建的对象需要的内存如何分配出来，也不用考虑在对象不再被使用后怎样释放它们所占用的内存。
Lua 运行了一个垃圾收集器来收集所有死对象（即在Lua中不能再访问到的对象）来完成自动内存管理的工作。Lua中所有用到的内存，如：字符串、表、用户数据、函数、线程、内部结构等，都服从内存管理。
Lua 实现了一个增量标记-扫描收集器。它使用这两个数字来控制垃圾收集循环：垃圾收集器间歇率和垃圾收集器步进倍率。这两个数字都使用百分数为单位（例如:值100在内部表示1）。

垃圾收集器间歇率控制着收集器需要在开启新的循环前要等待多久。增大这个值会减少收集器的积极性。当这个值比100小的时候，收集器在开始新的循环前不会有等待。设置这个值为200就会让收集器等到内存使用量达到之前的两倍时才开始新的循环。

垃圾收集器步进倍率控制着收集器运作速度相对于内存分配速度的倍率。增大这个值不仅会让收集器更加积极，还会增加每个增量步骤的长度。不要把这个值设的小于100，那样的话收集器就工作的太慢以至于干不完一个循环。默认值是200，表示收集器以内存分配的“两倍”速工作。

如果你把步进倍率设为一个非常大的数字 （比你的程序可能用到的字节数还大 10% ）， 收集器的行为就像一个 stop-the-world 收集器。 接着你若把间歇率设为 200 ， 收集器的行为就和过去的 Lua 版本一样了： 每次 Lua 使用的内存翻倍时，就做一次完整的收集。

### 垃圾回收器函数

Lua 提供了一下函数 collectgarbage([opt [, arg]]) 用来控制自动内存管理。

- collectgarbage("collect") : 做一次完整的垃圾收集循环。通过参数opt它提供了一组不同的功能。
- collectgarbage("count") : 以k字节数为单位返回 Lua 使用的总内存数。这个值有小数部分，所以只需要乘以1024就能得到Lua使用的准确字节数（除非溢出）
- collectgarbage("restart") : 重启垃圾收集器的自动运行。
- collectgarbage("setpause") : 将arg设为收集器的间歇率。返回间歇率的前一个值。
- collectgarbage("step") : 单步运行垃圾收集器。步长“大小”由arg控制。传入0时，收集器步进（不可分割）一步。传入非0值，收集器相当于Lua分配这些多（k字节）内存的工作。如果收集器结束一个循环将返回true。
- collectgarbage("stop") : 停止垃圾收集器的运行。在调用重启前，收集器只会因显式地调用运行。

示例：
```lua
> mytable = {"apple", "orange", "banana"}
> print(collectgarbage("count"))
58.8583984375
> mytable = nil
> print(collectgarbage("count"))
59.8076171875
> print(collectgarbage("collect"))
0
> print(collectgarbage("count"))
30.326171875
```

## Lua 面向对象

面向对象特征：

- 封装： 指能够把一个实体的信息、功能、响应都装入一个单独地对象中的特性。
- 继承： 继承的方法允许在不改动源程序的基础上对其进行扩充，这样使得原功能得以保存，而新功能也得以扩展。这有利于减少重复编码，提高软件的开发效率。
- 多态： 同意操作作用不同的对象，实现不同的行为。
- 抽象： 简化复杂的现实问题。

### Lua 中面向对象

Lua 中function可以用来表示方法。那么Lua中的类可以通过table+function模拟出来。
至于继承可以通过metatable模拟出来（不推荐用，只模拟最基本的对象大部分时间够用了）

```lua
Account = {balance = 0}
function Account.withdraw(v)
    Account.balance = Account.balance - v
end
```

这个定义创建了一个新的函数，并且保存在Account对象的withdraw域内，可以这样调用：
```lua
Account.withdraw(100.00)
```

#### 一个简单示例

以下简单示例包含了三个属性： area、length和breadth， printArea用于打印计算结果：
```lua
-- Meta Class
Rectangle = {area =0, length = 0, breadth = 0}

-- 派生类的方法 new
function Rectangle:new(o, length, breadth)
    o = o or {}
    setmetatable(o, self)
    self.__index = self
    self.length = length or 0
    self.breadth = breadth or 0
    self.area = length*breadth
    return o
end

-- 派生类的方法 printArea
function Rectangle:printArea()
    print("矩形面积为：", self.area)
end
```

#### 创建对象

创建对象是为类的实例分配内存的过程。每个类都有属于自己的内存并共享公共数据。
```lua
r = Rectangle:new(nil, 10, 20)
```
#### 访问属性

可以使用点符.来访问类的属性：
```lua
print(r.length)
```

#### 访问成员函数

我们可以使用冒号：来访问类的成员函数：
```lua
> r:printArea()
矩形面积为：    200
```

#### Lua 继承实例

```lua
 -- Meta class
Shape = {area = 0}
-- 基础类方法 new
function Shape:new (o,side)
  o = o or {}
  setmetatable(o, self)
  self.__index = self
  side = side or 0
  self.area = side*side;
  return o
end
-- 基础类方法 printArea
function Shape:printArea ()
  print("面积为 ",self.area)
end
```

#### 函数重写

Lua中我们可以重写基础类的函数，在派生类中定义自己的实现方式：
```lua
-- 派生类方法 printArea
function Square:printArea ()
  print("正方形面积 ",self.area)
end
```


## Lua 数据库访问

Lua 操作数据库的操作库： LuaSQL。是开源的支持ODBC, ADO, Oracle, MySQL, SQLite 和 PostgreSQL。
LuaSQL 可以使用 [LuaRocks](https://luarocks.org/) 来安装可以根据需要安装你需要的数据库驱动。
LuaRocks 安装方法：
```shell
$ wget http://luarocks.org/releases/luarocks-2.2.1.tar.gz
$ tar zxpf luarocks-2.2.1.tar.gz
$ cd luarocks-2.2.1
$ ./configure; sudo make bootstrap
$ sudo luarocks install luasocket
$ lua
Lua 5.3.0 Copyright (C) 1994-2015 Lua.org, PUC-Rio
> require "socket"

```


安装不同数据库驱动：
```lua
luarocks install luasql-sqlite3
luarocks install luasql-postgres
luarocks install luasql-mysql
luarocks install luasql-sqlite
luarocks install luasql-odbc
```

Lua 连接MySQL实力：
```lua
require "luasql.mysql"

--创建环境对象
env = luasql.mysql()

--连接数据库
conn = env:connect("数据库名","用户名","密码","IP地址",端口)

--设置数据库的编码格式
conn:execute"SET NAMES UTF8"

--执行数据库操作
cur = conn:execute("select * from role")

row = cur:fetch({},"a")

--文件对象的创建
file = io.open("role.txt","w+");

while row do
    var = string.format("%d %s\n", row.id, row.name)

    print(var)

    file:write(var)

    row = cur:fetch(row,"a")
end


file:close()  --关闭文件对象
conn:close()  --关闭数据库连接
env:close()   --关闭数据库环境
```

