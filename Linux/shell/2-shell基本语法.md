## Shell 基本语法

### shell 变量

#### 定义变量name：

>name="Byron"

**注意：**

- 变量名和=号之间不能有空格

#### 使用变量

在变量名前面加上美元$符号，即可引用已经定义过的变量：

```shell
name="Byron"

# 引用变量

echo $name

echo ${name}
```

花括号是为了帮助解释器识别变量的边界，如：

```shell
name="Byron"

echo "My name is ${name}"
```

#### 重新赋值不能使用$符号

```shell
your_name="tom"
echo $your_name
your_name="alibaba"
echo $your_name

```

变量your_name被二次赋值，是可以的。
**注意：** 但是，第二次赋值的时候不能写 $your_name="alibaba"，因为只有在引用变量的时候采用$符号。

#### 只读变量 readonly

readonly 命令可以将变量定义为只读变量，只读变量的值不能被改变。

```shell
#!/bin/bash
name="Byron"
readonly name
name="Lucy"
```

运行结果为：
```shell
/tmp/839326580/main.sh: line 4: name: readonly variable

exit status 1
```

#### 删除变量 unset

使用 unset 命令可以删除变量：

>unset 变量名

变量被删除后不能再次使用。
unset 命令不能删除只读变量。

```shell
#!/bin/bash
name="Byron"
echo $name
unset name
echo $name
```

只会输出第一次的Byron。第二次不会有任何输出。

#### 变量类型

运行shell时，会同时存在三种类型：

- 局部变量：在脚本或命令中定义，仅在当前shell实例中有效，其他shell启动的程序不能访问局部变量。
- 环境变量： 所有的程序包括shell启动程序，都能访问环境变量。
- shell变量： shell变量是由shell程序设置的特殊变量。shell变量中有一部分是环境变量一部分是局部变量，这些保证了shell程序的正常执行。



### Shell 字符串

shell中的字符串可以使用单引号、双引号，也可以不用引号。

#### 单引号字符串

>str='hello,boy!'

```shell
[deploy@sz-local3 lff]$ name="Byron"
[deploy@sz-local3 lff]$ echo $name
Byron
[deploy@sz-local3 lff]$ echo 'My name is $name'
My name is $name
[deploy@sz-local3 lff]$ echo "My name is $name"
My name is Byron
```


```shell
[deploy@sz-local3 lff]$ echo 'shell is a 'good' program language'
shell is a good program language
```


**注意事项**：

- 单引号里的任何字符都会原样输出，所以不要在单引号里面引用变量，毫无意义
- 单引号字符串中不能出现单个的单引号（对单引号使用转义符也不行），但是可以成对出现，作为字符串拼接使用


#### 双引号字符串

```shell
your_name='Byron'
str="Hello, I know you are \"$your_name\"! \n"
echo -e $str
```

输出为：

```shell
Hello, I know you are "Byron"! 
```

**双引号的优点：**

- 双引号里可以引用变量
- 双引号里可以出现转义字符

使用 echo 可以看到 echo 的使用方法：
```shell
[deploy@sz-local3 lff]$ help echo
echo: echo [-neE] [arg ...]
    Write arguments to the standard output.
    
    Display the ARGs on the standard output followed by a newline.
    
    Options:
      -n        do not append a newline  # 不会追加一个新行
      -e        enable interpretation of the following backslash escapes # 使解释器对以下列表中的字符进行转义
      -E        explicitly suppress interpretation of backslash escapes # 明确告诉解释器不进行转义
    
    `echo' interprets the following backslash-escaped characters:
      \a        alert (bell)
      \b        backspace
      \c        suppress further output
      \e        escape character
      \f        form feed
      \n        new line
      \r        carriage return
      \t        horizontal tab
      \v        vertical tab
      \\        backslash
      \0nnn     the character whose ASCII code is NNN (octal).  NNN can be
        0 to 3 octal digits
      \xHH      the eight-bit character whose value is HH (hexadecimal).  HH
        can be one or two hex digits
    
    Exit Status:
    Returns success unless a write error occurs.
```


#### 拼接字符串

```shell
[deploy@sz-local3 lff]$ greeting="Hello"
[deploy@sz-local3 lff]$ name="Byron"
[deploy@sz-local3 lff]$ echo $greeting$name
HelloByron
```

#### 获取字符串长度

```shell
[deploy@sz-local3 lff]$ name="Byron"
[deploy@sz-local3 lff]$ echo ${name}
Byron
[deploy@sz-local3 lff]$ echo ${#name}
5
```

#### 截取字符串

示例：从字符串第二个字符开始截取4个字符:

```shell
[deploy@sz-local3 lff]$ string="Byron is a great man"
[deploy@sz-local3 lff]$ echo ${string:1:4}
yron
```
字符串索引下标是从0开始的。

#### 查找字符串

查找字符出现的位置，首位为1：
```shell
[deploy@sz-local3 lff]$ echo `expr index ${name} b`
0
[deploy@sz-local3 lff]$ echo `expr index ${name} B`
1
```

### Shell 数组

bash 支持一维数组（不支持多维数组），且没有限定数组的大小。
类似C语言，数组元素的下标由0开始编号。
获取数组中的元素要利用下标，下标可以是整数或算术表达式，其值应>=0。

#### 定义数组

shell中，用圆括号()表示数组，数组元素用空格符号分隔开：

>数组名=(ele1 ele2 ... elen)

```shell
arr=(1 2 3 4 5)
或者
arr2=(
1
2
3
4
5
)
```

**还可以单独定义数组的各个元素：**
```shell
arr3[0]=1
arr3[1]=2
arr3[2]=3
```

#### 读取数组

读取数组元素的语法一般为：

>${数组名[索引下标值]}

示例：

```shell
[deploy@sz-local3 lff]$ echo ${arr[0]} ${arr[1]}
1 2

[deploy@sz-local3 lff]$ val=${arr[3]}
[deploy@sz-local3 lff]$ echo $val
4
```

**@、星号** 符号作为数组索引下标可以获取数组中的所有元素，示例：

```shell
[deploy@sz-local3 lff]$ echo ${arr[@]}
1 2 3 4 5

[deploy@sz-local3 lff]$ echo ${arr[*]}
1 2 3 4 5
```

#### 获取数组长度

获取数组长度在获取素组所有元素的前面使用#即可。

示例：
```shell
[deploy@sz-local3 lff]$ echo ${#arr[@]}
5
[deploy@sz-local3 lff]$ echo ${#arr[*]}
5

[deploy@sz-local3 lff]$ strArr=("Lily" "Jim" "Lucy" "Lilei" "Hanmeimei")
[deploy@sz-local3 lff]$ echo ${strArr[@]}
Lily Jim Lucy Lilei Hanmeimei
[deploy@sz-local3 lff]$ echo ${#strArr[@]}
5
[deploy@sz-local3 lff]$ echo ${#strArr[1]}
3
```

### Shell 注释

单行注释以 **#** 开头的行即为注释，会被解释器忽略。

test.sh 文件内容如下：
```shell
#!/bin/bash

arr=(1,2,3,4,5) # 这是一行注释
echo ${arr[@]}
```
在test.sh同目录下执行：

```shell
[deploy@sz-local3 lff]$ sh ./test.sh
1,2,3,4,5
```

#### shell 多行注释

多行注释语法如下：

```shell
:<<EOF
注释内容...
注释内容...
EOF
```

EOF也可以使用其他符号：

```shell
:<<'
注释内容...
注释内容...
'

或者

:<<!
注释内容...
注释内容...
!
```

示例：

```shell
[deploy@sz-local3 lff]$ cat test.sh
#!/bin/bash

arr=(1,2,3,4,5) #这是一行注释
echo ${arr[@]}

:<<EOF

注释内容
随便怎么写
哈哈

EOF
```

```shell
[deploy@sz-local3 lff]$ cat test.sh
#!/bin/bash

arr=(1,2,3,4,5) #这是一行注释
echo ${arr[@]}

:<<EOF

注释内容
随便怎么写
哈哈

EOF


:<<A


注释聂荣
zhishjflkm
wrjokwrtwr
A
```

多行注释的语法，标识符可以由用户任意定义，符合命名规范即可：

```shell
:<<标识符
注释内容...
注释内容...
标识符
```





































