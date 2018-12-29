# Python3 基本语法

## 第一个 Python 程序

我们来看一看在不同编程模式下的执行Python程序。

### 交互模式编程

调用解释器，不需要传递脚本文件作为参数。

```python
# Linux:
python
Python 2.6.6 (r266:84292, Jul 23 2015, 15:22:56) 
[GCC 4.4.7 20120313 (Red Hat 4.4.7-11)] on linux2
Type "help", "copyright", "credits" or "license" for more information.
>>> 

# Windows:
C:\Users\BYRON.Y.Y>python
Python 3.6.3 (v3.6.3:2c5fed8, Oct  3 2017, 18:11:49) [MSC v.1900 64 bit (AMD64)] on win32
Type "help", "copyright", "credits" or "license" for more information.
>>>
```


#### Hello,Python! 程序

在 Python 提示符中输入以下代码并按 Enter 键。

```python
>>> print("Hello, Python!")
# 输出结果为：
Hello, Python!
```

### 脚本模式编程

通过语法 **python script.py** 执行script.py文件中的python脚本。
新建一个文件 hello.txt，里面写入一行代码：
```python
print ("Hello,Python!")
```
然后在命令行执行(Windows下的演示)：
```bat
C:\Users\BYRON.Y.Y>python C:\\Users\\BYRON.Y.Y\\Desktop\\hello.py
Hello,Python!
```

Linux下：

```shell
# 使用awk写入文本到hello.txt
awk 'BEGIN {print "print(\"Hello,Python!\")" > "hello.txt"}'
# 查看文件内容
cat hello.txt
# 当前文件内容 
print("Hello,Python!")
# 使用python执行脚本文件
$ python hello.txt 
# 输出结果：
Hello,Python!
```

## Python 标识符

Python 标识符以 小写字母a-z，大写字母A-Z， 下划线 _ 开头，后面跟着数字或者字母。
Python 不允许使用标点符号 如 &、$ 和 % 作为标识符的组成部分。
Python 是大小写敏感的编程语言。

一些标识符的命名规范：

- 类名的首字母大写。其他的标识符都是小写开头。
- 用一个下划线开始的标识符表示是私有的。
- 用两个下划线开始的标识符表示是强私有的。
- 如果标识符也是以两个下划线结尾的，表示是语言定义的规范。

## 保留关键字

Python 有一些关键字。这些关键字你不能将他们作为标识符，这些关键字都是小写的。

||||
|----|-----|----|
|and|exec|not|
|as|finally|or|
|assert|for|pass|
|break|from|print|
|class|global|<font color=red>raise</font>|
|continue|if|return|
|def|import|try|
|del|in|while|
|elif|is|with|
|else|lambda|yield|
|except|||


## 行缩进

Python 不是使用花括号作为代码块界定符的。Python 中的代码块是使用行缩进来表示的，行缩进在Python中是强制要求的。
缩进中的空格数是可变的，但是代码块中的所有语句都需要保持同样的空格数目。例如：

```python
if True:
   print ("True")

else:
   print ("False")
```

以下则是错误的代码书写方式：

```python
if True:
   print ("Answer")
   print ("True")

else:
   print "(Answer")
   print ("False")
```

下面看一段较长的缩进代码示例，以下代码仅仅用来展示行缩进示例而已：

```python
import sys

try:
   # open file stream
   file = open(file_name, "w")

except IOError:
   print ("There was an error writing to", file_name)
   sys.exit()
print ("Enter '", file_finish,)
print ("' When finished")

while file_text != file_finish:
   file_text = raw_input("Enter text: ")
   
   if file_text == file_finish:
      # close the file
	  file.close
      break
   file.write(file_text)
   file.write("\n")
file.close()
file_name = input("Enter filename: ")

if len(file_name) == 0:
   print ("Next time please enter something")
   sys.exit()

try:
   file = open(file_name, "r")

except IOError:
   print ("There was an error reading file")
   sys.exit()
file_text = file.read()
file.close()
print (file_text)
```

## 多行语句

Python 中的语句通常以新行结束。Python 允许连续行字符 **\\** 表示行在继续。例如：

```pyhon
>>> a = 14+\
    12+\
    20
# 输出结果为：
>>> a
46
```

如果语句包含 **[]、{}** ，则不需要使用连续行字符了。例如以下示例演示了给一个数组 arr 赋值：

```python
>>> arr = [1,
       2,
       4]
>>> arr
[1, 2, 4]
```


## Python 中的引号

Python 接收单引号 （**'**） ， 双引号 （**"** ）和 三引号 （**'''**、**"""**）表示字符字面量，以什么符号开始就应该以什么符号结束。
三引号符一般用于跨多行使用的场景。例如：

```python
word = 'word'
sentence = "This is a sentence."
paragraph = """This is a paragraph. It is
made up of multiple lines and sentences."""
```

## Python 中的注释

**#**  符号并且不是在一个字符串字面量中间，是在开始，则表示注释。所有在 **#** 后面物理行的所有内容都会被解释器忽略。

```python
# 第一个注释
print ("Hello, Python!") # 第二个注释
```


## 空白行

一行只有空白字符，也许存在注释，都会被Python解释器忽略。

在解释器交互时，你需要输入一空行去终止一个多行语句。

## 等待用户输入

下面的一行程序提示了：输入回车关闭。然后等待用户做出动作。

```python
input("\n\nPress the enter key to exit.")
```

input()函数从控制台获取用户输入的内容：
```python
>>> input("你好，请输入你的名字：")
你好，请输入你的名字：Byron
'Byron'
```

## 在一行中有多个语句

分号 **;** 允许在单行中编写多条语句，可以使用分号分隔多条语句。示例如下：
```python
import sys; x = 'foo'; sys.stdout.write(x + '\n')

>>> a = 1; b = 2; c = 3
>>> a
1
>>> b
2
>>> c
3
```


## 多个语句组作为套件

组成单个代码块的语句组在Python中称为套件。混合或复杂的语句，例如 if、while、def 和 class 需要标题行和套件。
标题行起始于关键字，终止于冒号 **:** ，接着一行或更多的语句组成套件。如下示例：

```python
if expression : 
   suite
elif expression : 
   suite 
else : 
   suite
```


## 命令行参数

许多程序运行时可以提供给你一些它们如何运行的基本信息。Python 可以允许你做到这一点，使用 **-h** 命令查看一些帮助信息。

```python
C:\Users\BYRON.Y.Y>python -h
usage: python [option] ... [-c cmd | -m mod | file | -] [arg] ...
Options and arguments (and corresponding environment variables):
-b     : issue warnings about str(bytes_instance), str(bytearray_instance)
         and comparing bytes/bytearray with str. (-bb: issue errors)
-B     : don't write .pyc files on import; also PYTHONDONTWRITEBYTECODE=x
-c cmd : program passed in as string (terminates option list)
-d     : debug output from parser; also PYTHONDEBUG=x
-E     : ignore PYTHON* environment variables (such as PYTHONPATH)
-h     : print this help message and exit (also --help)
-i     : inspect interactively after running script; forces a prompt even
         if stdin does not appear to be a terminal; also PYTHONINSPECT=x
-I     : isolate Python from the user's environment (implies -E and -s)
-m mod : run library module as a script (terminates option list)
-O     : optimize generated bytecode slightly; also PYTHONOPTIMIZE=x
-OO    : remove doc-strings in addition to the -O optimizations
-q     : don't print version and copyright messages on interactive startup
-s     : don't add user site directory to sys.path; also PYTHONNOUSERSITE
-S     : don't imply 'import site' on initialization
-u     : unbuffered binary stdout and stderr, stdin always buffered;
         also PYTHONUNBUFFERED=x
         see man page for details on internal buffering relating to '-u'
-v     : verbose (trace import statements); also PYTHONVERBOSE=x
         can be supplied multiple times to increase verbosity
-V     : print the Python version number and exit (also --version)
         when given twice, print more information about the build
-W arg : warning control; arg is action:message:category:module:lineno
         also PYTHONWARNINGS=arg
-x     : skip first line of source, allowing use of non-Unix forms of #!cmd
-X opt : set implementation-specific option
file   : program read from script file
-      : program read from stdin (default; interactive mode if a tty)
arg ...: arguments passed to program in sys.argv[1:]

Other environment variables:
PYTHONSTARTUP: file executed on interactive startup (no default)
PYTHONPATH   : ';'-separated list of directories prefixed to the
               default module search path.  The result is sys.path.
PYTHONHOME   : alternate <prefix> directory (or <prefix>;<exec_prefix>).
               The default module search path uses <prefix>\lib.
PYTHONCASEOK : ignore case in 'import' statements (Windows).
PYTHONIOENCODING: Encoding[:errors] used for stdin/stdout/stderr.
PYTHONFAULTHANDLER: dump the Python traceback on fatal errors.
PYTHONHASHSEED: if this variable is set to 'random', a random value is used
   to seed the hashes of str, bytes and datetime objects.  It can also be
   set to an integer in the range [0,4294967295] to get hash values with a
   predictable seed.
PYTHONMALLOC: set the Python memory allocators and/or install debug hooks
   on Python memory allocators. Use PYTHONMALLOC=debug to install debug
   hooks.

```

## 变量类型

变量只是存储值的**预留内存**位置。这意味着当你创建一个变量的时候，则在内存中预留了一些空间。

基于变量的数据类型，解释器会分配内存并决定预留内存存储什么内容。因此，通过分配不同的数据类型给变量，可以存储integer、decimal 和字符串。

## 分配值给变量

Python 变量不需要显示指定内存空间，当你分配一个值给一个变量时就会自动分配内存空间。等于符号 **=** 用于分配值给变量。

**=** 操作符左边是变量名，右边是需要分配给左边变量的值。例如：
```python
counter = 100          # An integer assignment
miles   = 1000.0       # A floating point
name    = "John"       # A string

print (counter)
print (miles)
print (name)
```

## 多重赋值

Python 支持将一个值同时分配给多个变量。
```python
a = b = c = 1
```

还支持将多个值分配给多个变量，例如：
```python
a, b, c = 1, 2, "John"
```

## 标准数据类型

存储在内存中的数据可能是多种类型的。例如，一个人的年龄，存放的是一个数值类型的值，他的地址存储为字符类型的。Python 有多种标准数据类型，用于定义他们可能的操作和可能的存储方法。
Python 有5种标准数据类型：

- Numbers 数值类型
- String 字符串类型
- List 列表
- Tuple 元组
- Dictionary 字典

## Python 的数值类型

Number 数据类型用于存储数值类型。当你分配一个值给变量的时候，一个Number对象就创建了。
例如：
```python
var1 = 1
var2 = 2
```

你可以使用 **del** 语句删除对象引用。**del** 语法如下：

```python
del var1[, var2[, var3[, ..., varN]]]
```

```python
del var1,var2
```

Python 支持3种不同类型的数值类型：

- **int** 符号整数
- **float** 浮点数
- **complex** 复数

Python3 种所有的整型数都是长整型的。因此，并没有像long这样的类型。


























