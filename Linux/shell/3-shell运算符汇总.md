## Shell 进阶学习

### Shell 传递参数

可以在执行shell脚本时，向脚本传递参数，脚本内获取参数的格式为： $n。  n代表一个数字，$1 为执行脚本的第一个参数，依此类推...
其中 **$0**  表示执行的文件名。
使用 vi test.sh 创建文件，并写入以下内容，保存并退出。
```shell
[deploy@sz-local3 lff]$ vi test.sh
#!/bin/bash


echo "Shell 传递参数示例："
echo "执行的文件名:$0"
echo "传入的第一个参数:$1"
echo "传入过的第二个参数:$2"
```

执行脚本命令

```shell
[deploy@sz-local3 lff]$ chmod +x test.sh  # 为脚本设置可执行权限
[deploy@sz-local3 lff]$ ./test.sh 1 2 3  # 执行脚本文件
Shell 传递参数示例：
执行的文件名:./test.sh
传入的第一个参数:1
传入过的第二个参数:2
```

特殊含义的参数：

|参数|描述|
|----|----|
|$#|传递到脚本的参数|
|$*|以一个单字符串显示所有向脚本传递的参数。如"$*"用''括起来的情况，以"$1 $2 ... $n" 的形式输出所有参数|
|$$|脚本运行的当前进程ID号|
|$!|后台运行的最后一个进程ID号|
|$@|与"$*"相同，但是使用时加引号，并在引号中返回每个参数，以 "$1" "$2" ... "$n" 的形式输出所有参数|
|$-|显示shell使用的当前选项，与set命令功能相同|
|$?|显示最后命令的退出状态。0表示没有错误，其他任何值表明有错误|


```shell

[deploy@sz-local3 lff]$ cat test.sh 
#!/bin/bash


echo "Shell 传递参数示例："
echo "执行的文件名:$0"
echo "传入的第一个参数:$1"
echo "传入过的第二个参数:$2"



echo "传递到脚本的参数:$#"
echo "单字符串显示参数:$*"
echo "当前进程ID号:$$"
echo "后台运行的最后一个ID号:$!"
echo "各自引号的显示参数:$@"
echo "shell的当前选项:$-"
echo "显示最后命令的退出状态:$?"


# 执行脚本输出：
[deploy@sz-local3 lff]$ ./test.sh 1 2 3
Shell 传递参数示例：
执行的文件名:./test.sh
传入的第一个参数:1
传入过的第二个参数:2
传递到脚本的参数:3
单字符串显示参数:1 2 3
当前进程ID号:27808
后台运行的最后一个ID号:
各自引号的显示参数:1 2 3
shell的当前选项:hB
显示最后命令的退出状态:0
```


### Shell 数组

shell数组只支持一维数组，初始化不需要定义数组大小，数组元素下标从0开始。

shell素组用括号来表示，元素用空格符号分隔开，语法格式如下:

>arr=(ele1 ele2 ... elen)

示例：
```shell
arr=(A B "C" 1)
```

也可以使用下标来定义数组：

arr[0]=A
arr[1]=b


#### 读取数组

>${arr[index]}

```shell
[deploy@sz-local3 lff]$ arr=(1 2 3 4 5 )
[deploy@sz-local3 lff]$ echo ${arr[0]} ${arr[1]}
1 2
```

#### 获取数组中的所有元素

使用 @、* 作为索引下标，可以获取数组中所有元素。

```shell
[deploy@sz-local3 lff]$ echo ${arr[@]}
1 2 3 4 5
[deploy@sz-local3 lff]$ echo ${arr[*]}
1 2 3 4 5
```

#### 获取数组长度

获取数组长度和获取字符串长度一样，使用#，需要指定全数组全部元素。

```shell
[deploy@sz-local3 lff]$ echo ${#arr[@]}
5
```

### Shell 基本运算符

shell 和其他编程语言一样，支持多种运算符，包括：

- 算术运算符
- 关系运算符
- 布尔运算符
- 字符串运算符
- 文件测试运算符

原生bash不支持简单的数学运算，但是可以通过其他命令实现，例如awk和expr，expr最常用。
expr是一款表达式计算工具，使用它可以完成表达式的求值操作。
使用单反引号。

示例：

```shell
[deploy@sz-local3 lff]$ val=`expr 1 + 2`
[deploy@sz-local3 lff]$ echo $val
3
```

**注意：**
 
- 表达式和运算符之间要有空格，例如写成1+2是不对的，这一点比较恶心把...
- 完整的表达式要被 `` 包含，注意这个字符是反引号。

#### 算术运算符

假定变量a为10、b为20。

|运算符|说明|示例|
|----|----|----|
|+|加法|`expr $a + $b` 结果为30|
|-|减法|`expr $a - $b` 结果为-10|
|*|乘法|`expr $a \* $b` 结果为200|
|/|除法|`expr $b / $a` 结果为2|
|%|取余|`expr $a % $b` 结果为10|
|=|赋值|a=$b 将变量b的值赋给a|
|==|是否相等，相等则返回true|**[ $a == $b ]** 返回false|
|!=|不相等，不等则返回true|**[ $a != $b ]** 返回true|

**注意：** 条件表达式放在方括号之间，必须包含空格。 必须写成 **[ $a == $b ]**。

示例：

```shell
[deploy@sz-local3 lff]$ echo `expr $a + $b`
30
[deploy@sz-local3 lff]$ echo `expr $a - $b`
-10
[deploy@sz-local3 lff]$ echo `expr $a \* $b`
200
[deploy@sz-local3 lff]$ echo `expr $a / $b`
0
[deploy@sz-local3 lff]$ echo `expr $a % $b`
10

if [ $a == $b ]
then
   echo "a 等于 b"
fi
if [ $a != $b ]
then
   echo "a 不等于 b"
fi

输出：
a 不等于 b
```

**注意：** amc中expr的语法是： **$((表达式))**，此处表达式的*不需要转义。

#### 关系运算符

关系运算符只支持数字，不支持字符串，除非该字符串的值是数值形式，依然假定a为10，b为20.


- -eq ：检测两个数是否相等,相等返回true，[ $a -eq $b ] 返回false。
- -ne ： 是否不相等，不相等则返回true
- -gt ： 是否大于
- -lt ： 是否小于
- -ge ： 是否大于等于
- -le ： 是否小于等于

```shell
a=10
b=20

if [ $a -eq $b ]
then
   echo "$a -eq $b : a 等于 b"
else
   echo "$a -eq $b: a 不等于 b"
fi
if [ $a -ne $b ]
then
   echo "$a -ne $b: a 不等于 b"
else
   echo "$a -ne $b : a 等于 b"
fi
if [ $a -gt $b ]
then
   echo "$a -gt $b: a 大于 b"
else
   echo "$a -gt $b: a 不大于 b"
fi
if [ $a -lt $b ]
then
   echo "$a -lt $b: a 小于 b"
else
   echo "$a -lt $b: a 不小于 b"
fi
if [ $a -ge $b ]
then
   echo "$a -ge $b: a 大于或等于 b"
else
   echo "$a -ge $b: a 小于 b"
fi
if [ $a -le $b ]
then
   echo "$a -le $b: a 小于或等于 b"
else
   echo "$a -le $b: a 大于 b"
fi
```



#### 布尔运算符

同样假定 a=10，b=20.

|布尔运算符|描述|示例|
|----|----|----|
|!|非运算|[ !false ] 结果为true|
|-o|或运算|[ $a -lt $b -o $a -gt 5 ] ：10小于20或者10大于5|
|-a|与运算|[ $a -ge 5 -a $b -ge 5 ] : 10大于等于5并且20大于等于5|

示例：

```shell
[deploy@sz-local3 lff]$ if  [ $a -lt $b -o $a -gt 5 ]
> then
> echo '10小于20或者10大于5:true'
> fi
10小于20或者10大于5:true
[deploy@sz-local3 lff]$ 
[deploy@sz-local3 lff]$ 
[deploy@sz-local3 lff]$ 
[deploy@sz-local3 lff]$ if [ $a -ge 5 -a $b -ge 5 ]
> then
> echo '10大于等于5并且20大于等于5'
> fi
10大于等于5并且20大于等于5
```


#### 逻辑运算符

同样假定a=10，b=20.

|运算符|描述|示例|
|----|----|----|
|&&|逻辑的AND|[[ $a -lt 100 && $b -gt 100 ]] 返回false|
|&#124;&#124;|逻辑的OR|[[ $a -lt 100 &#124;&#124; $b -gt 100 ]] 返回true|

```shell
a=10
b=20

if [[ $a -lt 100 && $b -gt 100 ]]
then
   echo "返回 true"
else
   echo "返回 false"
fi

if [[ $a -lt 100 || $b -gt 100 ]]
then
   echo "返回 true"
else
   echo "返回 false"
fi
```


**if...else...fi** 语句是shell的分支语法，后面讲到。

#### 字符串运算符

假定a="abc"，b="efg"。

|运算符|描述|示例|
|----|----|----|
|=|检测两个字符串是否相等，相等返回true|[ $a = $b ]|
|!=|检测两个字符串是否不等，不等则返回true|[ %a != $b ]|
|-z|检测字符串长度是否为0，为0返回true|[ -z $a]|
|-n|检测字符串长度是否不为0，不为0返回true|[ -n $a ]|
|str|检测字符串是否不为空，不为空返回true|[ $a ] 返回true|


```shell
a="abc"
b="efg"

if [ $a = $b ]
then
   echo "$a = $b : a 等于 b"
else
   echo "$a = $b: a 不等于 b"
fi
if [ $a != $b ]
then
   echo "$a != $b : a 不等于 b"
else
   echo "$a != $b: a 等于 b"
fi
if [ -z $a ]
then
   echo "-z $a : 字符串长度为 0"
else
   echo "-z $a : 字符串长度不为 0"
fi
if [ -n "$a" ]
then
   echo "-n $a : 字符串长度不为 0"
else
   echo "-n $a : 字符串长度为 0"
fi
if [ $a ]
then
   echo "$a : 字符串不为空"
else
   echo "$a : 字符串为空"
fi

[deploy@sz-local3 lff]$ if [ -z $a ]
> then
> echo "a长度为0"
> else
> echo "a长度不为0"
> fi
a长度不为0



[deploy@sz-local3 lff]$ if [ -n $a ]
> then
> echo "a长度不为0"
> else
> echo "a长度为0"
> fi
a长度不为0


[deploy@sz-local3 lff]$ if [ $a ]
> then
> echo "a不为空"
> else
> echo "a为空"
> fi
a不为空
```


#### 文件测试运算符

文件测试运算符用于检测linux系统文件的各种属性

|操作|描述|示例|
|----|----|----|
|-b file|检测文件是否是块设备文件|[ -b $file ]|
|-c file|检测文件是否是字符设备文件| [ -c $file ] |
|-d file|检测是否为目录|[ -d $file ]|
|-f file|检测文件是否为普通文件| [ -f $file ] |
|-g file|检测文件是否设置了SGID位| [-g $file] |
|-k file|检测文件是否设置了粘着位(Sticky Bit)| [ -l $file ] |
|-p file|检测文件是否有名管道|[ -p $file ]|
|-u file|检测文件是否设置了SUID位|[ -u $file ]|
|-r file|检测文件是否可读|[ -r $file ]|
|-w file|检测文件是否可写|[ -w $file ]|
|-x file|检测文件是否可执行|[ -x $file ]|
|-s file|检测文件是否为空(文件大小是否为0)|[ -s $file ]|
|-e file|检测文件是否存在|[ -e $file ]|



```shell
file="hello.txt"

if [ -r $file ]
then
   echo "文件可读"
else
   echo "文件不可读"
fi
if [ -w $file ]
then
   echo "文件可写"
else
   echo "文件不可写"
fi
if [ -x $file ]
then
   echo "文件可执行"
else
   echo "文件不可执行"
fi
if [ -f $file ]
then
   echo "文件为普通文件"
else
   echo "文件为特殊文件"
fi
if [ -d $file ]
then
   echo "文件是个目录"
else
   echo "文件不是个目录"
fi
if [ -s $file ]
then
   echo "文件不为空"
else
   echo "文件为空"
fi
if [ -e $file ]
then
   echo "文件存在"
else
   echo "文件不存在"
fi
```

























