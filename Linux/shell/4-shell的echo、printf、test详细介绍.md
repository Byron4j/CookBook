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


















