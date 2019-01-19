## Shell 基础

Shell 是一个用 C 语言编写的程序，是用户使用 Linux 的桥梁。
Shell 既是一种命令语言，又是一种程序设计语言。


### Shell 环境

Shell 编程和java、php一样，只要有一个编写代码文本编辑器和一个能解释执行脚本解释器就可以了。
Linux 的shell种类繁多，常见的有：

- Bourne Shell（/usr/bin/sh或/bin/sh）
- Bourne Again Shell（/bin/bash）
- C Shell（/usr/bin/csh）
- K Shell（/usr/bin/ksh）
- Shell for Root（/sbin/sh）
- ……

我们使用 Bash，亦即 Bourne Again Shell，易用免费，也是大都数 Linux 系统默认的 shell。
一般情况下，人们不太区分sh和bash，所以像 **#!/bin/sh** ,也同样可以改为 **#!/bin/bash**

**#!** 命令告诉系统其后路径所指定的程序即为此脚本文件的shell程序。

### 第一个 shell 脚本

shell脚本扩展名为 **.sh**。

使用 vi test.sh 创建shell脚本文件，并写入

```shell
#!/bin/bash

echo "Hello World!"
```

再执行 **sh ./test.sh** 将输出：

```shell
byronyys-MacBook-Pro:CookBook byronyy$ sh ./test.sh 
Hello World!
```

**echo** 用于向窗口输出文件。


### 运行shell脚本有两种方法

#### 作为可执行程序

- 1.将 以下内容保存为 test.sh ：

```shell
#!/bin/bash

echo "Hello World!"
```

- 2. 并 cd 到对应目录：

```shell
chmod +x ./test.sh # 使得该脚本文件具有执行权限
./test.sh  #执行脚本
```

注意，此处需要写成 **./test.sh**，而不是 **test.sh**。
直接写成 test.sh ， linux 系统会去PATH里寻找有没有test.sh文件，
而只有 /bin、/sbin、/usr/bin、/usr/sbin 等在PATH中，当前目录一般不在PATH中，所以需要写成 **./test.sh** 告知系统就在当前目录中找。

### 作为解释器参数

直接运行解释器，后面将shell脚本文件名作为参数：

```shell
byronyys-MacBook-Pro:CookBook byronyy$ /bin/sh test.sh 
Hello World!
```

**注意：** 这种方式运行的脚本，不需要在第一行制定解释器信息，写了也没用，还是以该方式为准。

