## Shell的echo、printf、test详细介绍

### echo 命令介绍

shell 的echo 指令与PHP的echo指令类似，都用于字符串的输出。

>echo string

#### 显示普通字符串

```shell
[deploy@sz-local3 lff]$ echo "Lucy is beauty"
Lucy is beauty
```

#### 显示转义字符

```shell
[deploy@sz-local3 lff]$ echo "\"大家好，才是真的好！\""
"大家好，才是真的好！"
```

#### 显示变量

**read** 用于从标准输入流中读取一行，并把输入行的没个字段的值制定给shell变量。

```shell
[deploy@sz-local3 lff]$ read name
亭亭玉立
[deploy@sz-local3 lff]$ echo $name
亭亭玉立
```

#### 转义，显示换行

**echo -e** 开启转义。

```shell
[deploy@sz-local3 lff]$ echo -e "OK! \n"
OK!
 
```

#### 显示不换行

```shell
[deploy@sz-local3 lff]$ echo -e "OK! \c"
OK!
```

#### 显示结果定向到指定文件

```shell
echo "定向到文件的内容" > myfile

# 查看文件内容
[deploy@sz-local3 lff]$ cat myfile 
定向到文件的内容
```

#### 使用单引号，原样输出字符串

```shell
[deploy@sz-local3 lff]$ echo '$name\"'
$name\"
```

#### 使用反引号 显示命令执行结果

```shell
[deploy@sz-local3 lff]$ echo `date`
Wed Jan 23 18:26:35 CST 2019 
```




#### 可以使用 help echo 查看echo的使用方式介绍

```shell
[deploy@sz-local3 lff]$ help echo
echo: echo [-neE] [arg ...]
    Write arguments to the standard output.
    
    Display the ARGs on the standard output followed by a newline.
    
    Options:
      -n        do not append a newline # 输出显示后，不再追加一个新行
      -e        enable interpretation of the following backslash escapes  # 开启转义
      -E        explicitly suppress interpretation of backslash escapes # 下面列表的转义字符不再进行转义
    
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

### printf 指令
printf 类似C语言的printf函数。
printf 由 POSIX 标准所定义，移植性比echo更好。
printf 使用引用文本或空格分隔的参宿，可以使用格式化字符串、指定字符串的宽度、对齐方式等，默认情况下 printf 不会像echo自动添加换行符，可以手动添加\n。

>printf format-string [arguments...]

```shell
[deploy@sz-local3 lff]$ echo "Lucy is beauty."
Lucy is beauty.
[deploy@sz-local3 lff]$ printf "Lucy is beauty.\n"
Lucy is beauty.
```


printf 格式化示例：

文件内容如下shell脚本：

```shell
[deploy@sz-local3 lff]$ cat printf.txt 
#!/bin/sh
printf "%-10s %-8s %-4s\n" 姓名 性别 体重kg  
printf "%-10s %-8s %-4.2f\n" 郭靖 男 66.1234 
printf "%-10s %-8s %-4.2f\n" 杨过 男 48.6543 
printf "%-10s %-8s %-4.2f\n" 郭芙 女 47.9876 
```

添加执行权限、执行脚本：
```shell
[deploy@sz-local3 lff]$ chmod +x printf.txt 
[deploy@sz-local3 lff]$ ./printf.txt
姓名     性别   体重kg
郭靖     男      66.12
杨过     男      48.65
郭芙     女      47.99
```

**%s、%c、%d、%f** 都是格式替换符。

%-10s 指一个宽度为10个字符(-表示左对齐，没有则表示右对齐)，任何字符都会显示在10个字符宽度的字符范围内，如果不足则空格填充，超过也会把内容全部显示出来。

%-4.2f 指格式化为小数，其中.2指保留2位小数。

更多示例：

```shell
# format-string为双引号
printf "%d %s\n" 1 "abc"

# 单引号与双引号效果一样 
printf '%d %s\n' 1 "abc" 

# 没有引号也可以输出
printf %s abcdef

# 格式只指定了一个参数，但多出的参数仍然会按照该格式输出，format-string 被重用
printf %s abc def

printf "%s\n" abc def

printf "%s %s %s\n" a b c d e f g h i j

# 如果没有 arguments，那么 %s 用NULL代替，%d 用 0 代替
printf "%s and %d \n" 
```

输出：
```shell
1 abc
1 abc
abcdefabcdefabc
def
a b c
d e f
g h i
j  
 and 0 
```

### test 指令

shell 中的 test 指令用于检查某个条件是否成立，可以进行数值、字符、文件三个方面的测试。

#### 数值测试

有 -eq, -ne, -gt, -ge, -lt, -le 操作符：

```shell
a=100
b=100

if test $[a] -eq $[b]
then
    echo "a与b相等"
else 
    echo "a不等于b"
fi

输出：
a与b相等
```

**代码中的 []** 执行基本的算术运算，如：

```shell
[deploy@sz-local3 lff]$ a=5
[deploy@sz-local3 lff]$ b=6
[deploy@sz-local3 lff]$ echo $[a+b]
11
```

#### 字符串测试

字符串测试运算符： =, !=, -z, -n


```shell
num1="Joy"
num2="John"
if test $num1 = $num2
then
    echo '两个字符串相等!'
else
    echo '两个字符串不相等!'
fi
```
#### 文件测试符

文件测试符包含： 

- -e file
- -d file
- -r file
- -w file
- -x file
- -f file
...

更多参考前面的运算符章节。

```shell
if test -e ./bash
then
    echo '文件已存在!'
else
    echo '文件不存在!'
fi
```

还有 -a、-o、!  三个逻辑操作可以将测试条件连接起来。

```shell
cd /bin
if test -e ./notFile -o -e ./bash
then
    echo '至少有一个文件存在!'
else
    echo '两个文件都不存在'
fi
```












