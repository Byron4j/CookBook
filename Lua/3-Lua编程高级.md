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

print("main", coroutine.resume(co, 1, 10)) -- true, 4
print("--分割线----")
print("main", coroutine.resume(co, "r")) -- true 11 -9
print("---分割线---")
print("main", coroutine.resume(co, "x", "y")) -- true 10 end
print("---分割线---")
print("main", coroutine.resume(co, "x", "y")) -- cannot resume dead coroutine
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


