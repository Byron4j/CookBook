# Linux 基础

### Linux 启动流程

Linux 启动时会看到很多启动信息。
一般 Linux 启动分为5个阶段：
- 内核的引导 Boot
- 运行 init
- 系统初始化
- 建立终端
- 用户登陆系统

><i>init程序的类型</i>：
>
>**SysV**: init, CentOS 5 之前，配置文件： /etc/inittab
>
>**Upstart**: init, Cent OS 6, 配置文件： /etc/inittal,/etc/init/*.conf
>
>**Systemd**: systemd, Cent OS 7,配置文件： /use/lib/systemd/system、/etc/systemd/system

### Linux 常见的rc的含义

在linux中经常会碰到 bashrc,rc.d,rc.local等带有rc的文件或目录，一般都是启动时需要加载的。

- 英文原义：RC （runcom，run command）
- 中文释义：含有程序（应用程序甚至操作系统）启动指令的脚本文件 
- 注　　解：这一文件在操作系统启动时会自动执行，它含有要运行的指令（命令或其它脚本）列表。

真正的rc启动脚本实际上都是放在/etc/rc.d/init.d/目录下。
而这些rc启动脚本有着类似的用法，它们一般能接受start、stop、restart、status等参数。

### 用户登陆方式

一般来说，用户的登录方式有三种：
- （1）命令行登录
- （2）ssh登录
- （3）图形界面登录


Linux 的账号验证程序是 login，login 会接收 mingetty 传来的用户名作为用户名参数。
然后 login 会对用户名进行分析：如果用户名不是 root，且存在 /etc/nologin 文件，login 将输出 nologin 文件的内容，然后退出。
这通常用来系统维护时防止非root用户登录。只有/etc/securetty中登记了的终端才允许 root 用户登录，如果不存在这个文件，则 root 用户可以在任何终端上登录。
/etc/usertty文件用于对用户作出附加访问限制，如果不存在这个文件，则没有其他限制。


### Linux 文件属性

Linux 是一种典型的多用户系统，不同的用户处于不同的地位，拥有不同的权限。
为了保护系统的安全性，Linux系统对不同的用户访问同一文件（包括目录）的权限做了不同的规定。

在 Linux 中，可以使用 **ls -l** 命令来显示一个文件的属性以及文件所属的用户和组。如：

![ls-l命令示意图](ls-l.png)

```shell
ls -l
total 23028
-rw-rw-r--  1 deploy deploy        2 Dec 25 21:54 10
-rw-rw-r--  1 deploy deploy        0 Dec 18 16:48 6
-rw-rw-r--  1 deploy deploy    97119 Dec 20 23:52 arthas-boot.jar
-rw-rw-r--  1 deploy deploy      461 Dec 18 20:49 awkvars.out
-rw-rw-r--  1 deploy deploy        5 Dec 27 12:36 emp.awk
-rw-r--r--  1 deploy deploy       81 Dec 17 16:12 emp.data
-rw-r--r--  1 deploy deploy       23 Dec 29 12:35 hello.txt
-rw-rw-r--  1 deploy deploy    43379 Dec 28 12:27 index.html
-rw-r--r--  1 deploy deploy   109230 Dec 18 14:52 loan.txt
drwxr-xr-x  4 deploy deploy     4096 Jan  9 16:17 lua-5.3.0
-rw-rw-r--  1 deploy deploy   278045 Jan  6  2015 lua-5.3.0.tar.gz
drwxr-xr-x  4 deploy deploy     4096 Mar 18  2015 luarocks-2.2.1
-rw-rw-r--  1 deploy deploy   114431 Oct 31 04:52 luarocks-2.2.1.tar.gz
drwxr-xr-x 19 deploy deploy     4096 Dec 28 14:18 Python-3.7.2
-rw-rw-r--  1 deploy deploy 22897802 Dec 24 11:42 Python-3.7.2.tgz
```
在 Linux 中第一个字符代表这个文件时目录、文件或链接文件等等。

- 为 **d** 表示是目录
- 为 **-** 表示是文件
- 为 **l** 表示是链接文件（link file）
- 为 **b** 表示为装置文件里面的可供储存的接口设备（可随机存取装置）
- 为 **c** 表示为装置文件里面的串行端口设备，例如键盘、鼠标（一次性读取装置）


接下来的字符中，且均为 **rwx** 的三个参数的组合，其中 **r** 表示可读，**w** 表示可写， **x** 表示可执行。
这三个权限的位置不会改变，如果没有权限，就会出现减号 **-**。

![](rwx.png)


#### Linux 文件属性和属组

对于文件来说，它都有一个特定的所有者，也就是对该文件具有所有权的用户。
同时，在 Linux 系统中，用户是按组分类的，一个用户属于一个或多个组。
文件所有者以外的用户又可以分为文件所有者的同组用户和其他用户。

因此，Linux 系统按文件所有者、文件所有者同组用户和其它用户来规定了不同的文件访问权限。

```shell
ls -l
total 64
drwxr-xr-x 2 root  root  4096 Feb 15 14:46 cron
drwxr-xr-x 3 mysql mysql 4096 Apr 21  2014 mysql
```

在上面实例中，mysql 文件是一个目录文件，属主和属组都为 mysql，属主有可读、可写、可执行的权限；
与属主同组的其他用户有可读和可执行的权限；其它用户也有可读和可执行的权限。

对于 root 用户，一般情况下，文件的权限对其不起作用。

#### 更改文件属性

##### 1. chgrp 更改文件属组

> chgrp [-R] 属组名 文件名

- **-R** ： 递归更改文件属组，就是在更改某个目录文件的属组时，如果加上 **-R** 的参数，那么该目录下的所有文件的属组都会更改。


##### 2. chown 更改文件属主，也可以同时更改文件属组

>chown [-R] 属主名 文件名
>
>chown [-R] 属主名:属组名 文件名

进入 /root 目录（~）将install.log的拥有者改为bin这个账号：

```shell
[root@www ~] cd ~
[root@www ~]# chown bin install.log
[root@www ~]# ls -l
-rw-r--r--  1 bin  users 68495 Jun 25 08:53 install.log
```

将install.log的拥有者与群组改回为root：

```shell
[root@www ~]# chown root:root install.log
[root@www ~]# ls -l
-rw-r--r--  1 root root 68495 Jun 25 08:53 install.log
```

##### chmode: 更改文9个属性

Linux 文件属性有2中设置方法，一种是数字，一种是符号。
Linux 文件的基本权限有9个。分别是owner/group/others三种身份各有自己的read/write/execute权限。
文件权限字符为 -rwxrwxrwx 这九个权限是三个三个一组的。其中，我们可以使用数字来代表各个权限，个权限的分数对照表如下：

- r:  4
- w:  2
- x:  1

每种身份各自的三个权限分数是需要累加的。 例如 -rwxrwx--- 分数则为：

- owner: rwx = 4+2+1 = 7
- group: rwx = 4+2+1 = 7
- others:--- = 0+0+0 = 0

所以该文件的权限数字就是770了。

变更权限的指令chmod语法是这样的：

>chmod [-R] xyz 文件或目录

选项与参数：
- xyz: 就是刚刚提到的数字类型的权限属性，为 rwx 属性数值的相加。
- -R ： 进行递归的持续变更，亦即连同次目录下的所有文件都会变更

实例，将 .hashrc 文件的所有权限都设定为启用，则命令如下：

```shell
[root@www ~]# ls -al .bashrc
-rw-r--r--  1 root root 395 Jul  4 11:45 .bashrc

# 777表示 -rwxrwxrwx 的权限符号表示
[root@www ~]# chmod 777 .bashrc     
[root@www ~]# ls -al .bashrc
-rwxrwxrwx  1 root root 395 Jul  4 11:45 .bashrc
```


**符号类型改变文件权限**

还有一个改变权限的方法，从之前的介绍中我们可以发现基本上就9个权限分别是：

- owner
- group
- others

那么我们可以用 **u**、**g** 和 **o** 来代表三种身份的权限！

如果我们要将文件设置为 **-rwxr-xr--** ，可以使用 **chmod u=rwx,g=rx,o=r 文件名**  来设定：

```shell
[deploy@sz-local3 lff]$ ll hello.txt 
-rwxrwx--- 1 deploy deploy 23 Dec 29 12:35 hello.txt
[deploy@sz-local3 lff]$ chmod u=rwx,g=rwx,o=rwx hello.txt 
[deploy@sz-local3 lff]$ ll hello.txt 
-rwxrwxrwx 1 deploy deploy 23 Dec 29 12:35 hello.txt
```

拿掉全部人的可执行权限：

```shell
[deploy@sz-local3 lff]$ ll hello.txt 
-rwxrwxrwx 1 deploy deploy 23 Dec 29 12:35 hello.txt
[deploy@sz-local3 lff]$ chmod a-x hello.txt 
[deploy@sz-local3 lff]$ ll hello.txt 
-rw-rw-rw- 1 deploy deploy 23 Dec 29 12:35 hello.txt
```

更多实例：
```shell
[deploy@sz-local3 lff]$ ll hello.txt 
-rw-rw-rw- 1 deploy deploy 23 Dec 29 12:35 hello.txt

[deploy@sz-local3 lff]$ chmod a+rwx hello.txt 
[deploy@sz-local3 lff]$ ll hello.txt 
-rwxrwxrwx 1 deploy deploy 23 Dec 29 12:35 hello.txt

[deploy@sz-local3 lff]$ chmod u-x,g-x,o-x hello.txt 
[deploy@sz-local3 lff]$ ll hello.txt 
-rw-rw-rw- 1 deploy deploy 23 Dec 29 12:35 hello.txt
```


### Linux 文件与目录管理

#### 处理目录的常用命令

- ls： 列出目录
- cd ： 切换目录
- pwd ： 显示当前目录
- mkdir ： 创建一个新的目录
- rmdir ： 删除一个空目录
- cp ： 复制文件或目录
- rm ： 移除文件或目录
- mv ： 移动文件或目录，或修改文件与目录的名称

可以使用 **man [命令]** 来查看各个命令的使用文档，如： man cp等。

##### ls （列出目录）

>ls [optional] 目录名称

选项与参数：

- -a : 全部的文件，连同隐藏（开头为 . 的文件）一起列出来
- -d ： 仅仅列出目录本身，而不是列出目录内的文件内容
- -l ：长数据串列出，包含文件的属性与权限等数据

##### cd （切换目录）

cd 是 Change Directory 的缩写。

>cd [相对路径或绝对路径]

##### pwd (显示当前所在的目录)

>pwd [-P]

- **-P** : 显示出确实的路径，而非使用连接路径

##### mkdir （创建新目录）

>mkdir [-mp] 目录名称

选项参数：
- -m : 配置文件的权限！直接配置，无需使用默认权限。
- -p ： 帮助你直接将所需要的目录（包含上一级目录递归创建）

```shell
[deploy@sz-local3 lff]$ mkdir test #创建目录test
[deploy@sz-local3 lff]$ ls
10  arthas-boot.jar  emp.awk   hello.txt   loan.txt   lua-5.3.0.tar.gz  luarocks-2.2.1.tar.gz  Python-3.7.2.tgz  tmpDir
6   awkvars.out      emp.data  index.html  lua-5.3.0  luarocks-2.2.1    Python-3.7.2           test
[deploy@sz-local3 lff]$ mkdir test1/test2/test3  #创建连接目录，失败
mkdir: cannot create directory `test1/test2/test3': No such file or directory
[deploy@sz-local3 lff]$ mkdir -p  test1/test2/test3  #加上-p参数后，创建连接目录OK
[deploy@sz-local3 lff]$ ls
10  arthas-boot.jar  emp.awk   hello.txt   loan.txt   lua-5.3.0.tar.gz  luarocks-2.2.1.tar.gz  Python-3.7.2.tgz  test1
6   awkvars.out      emp.data  index.html  lua-5.3.0  luarocks-2.2.1    Python-3.7.2           test              tmpDir
```


**实例：** 创建权限为 **rwx--x--x** 的目录：

```shell
[deploy@sz-local3 test1]$ mkdir test2
[deploy@sz-local3 test1]$ ll
total 4
drwxrwxr-x 2 deploy deploy 4096 Jan 15 19:55 test2
[deploy@sz-local3 test1]$ mkdir -m 711 test7  # 指定权限 rwx--x--x
[deploy@sz-local3 test1]$ ll
total 8
drwxrwxr-x 2 deploy deploy 4096 Jan 15 19:55 test2
drwx--x--x 2 deploy deploy 4096 Jan 15 19:56 test7
```

##### rmdir （删除空的目录）

>rmdir [-p] 目录名称

选项参数：

- -p : 连同上一级（空的）目录也一起删除

```shell
# 创建连接目录
[deploy@sz-local3 test1]$ mkdir -p tst1/tst2/tst3/tst4   
[deploy@sz-local3 test1]$ ls
tst1

# 使用 rmdir 命令尝试删除，无法删除存在内容的目录 
[deploy@sz-local3 test1]$ rmdir tst1
rmdir: failed to remove `tst1': Directory not empty
[deploy@sz-local3 test1]$ rmdir -p tst1
rmdir: failed to remove `tst1': Directory not empty
# 使用 rmdir -p 删除上一级空目录
[deploy@sz-local3 test1]$ rmdir -p tst1/tst2/tst3/tst4
```

**注意：** rmdir 仅能删除空的目录，使用 rm 命令可以来删除非空目录。

##### cp （复制文件或目录）

>cp [-adfilprsu] 来源 目标

选项与参数：
- -a : 相当于 -pdr。
- -d ： 若来源为连接属性（link file），则复制连接档属性而非文件本身
- -f ： 为强制的意思，若目标文件已经存在且无法开启，则移除后再尝试一次
- **-i** ： 若目标档已存在，在覆盖时会先询问动作的进行
- -l ： 进行硬式连结(hard link)的连结档创建，而非复制文件本身；
- **-p** ： 连同文件的属性一同复制过去，而非使用默认属性
- **-r** ： 递归复制，用于目录的复制行为
- -s ： 复制成为符号链接档，即快捷方式
- -u ： 若目标比源旧才升级目标档

```shell
[deploy@sz-local3 test1]$ ll
total 0
-rwxrwxrwx 1 deploy deploy 0 Jan 15 20:21 hello.txt
[deploy@sz-local3 test1]$ cp hello.txt hello2.txt
[deploy@sz-local3 test1]$ ll
total 0
-rwxrwxr-x 1 deploy deploy 0 Jan 15 20:26 hello2.txt    #默认属性
-rwxrwxrwx 1 deploy deploy 0 Jan 15 20:21 hello.txt
[deploy@sz-local3 test1]$ cp -p hello.txt hello3.txt    #连同属性赋值
[deploy@sz-local3 test1]$ 
[deploy@sz-local3 test1]$ ll
total 0
-rwxrwxr-x 1 deploy deploy 0 Jan 15 20:26 hello2.txt
-rwxrwxrwx 1 deploy deploy 0 Jan 15 20:21 hello3.txt
-rwxrwxrwx 1 deploy deploy 0 Jan 15 20:21 hello.txt
```

##### rm （移除文件或目录）

>rm [-fir] 文件或目录

选项与参数：
- -f  ： 忽略不存在的文件，不会出现警告信息
- -i ： 互动模式，在删除前会询问使用者是否动作
- -r ： 递归删除。

```shell
[deploy@sz-local3 test1]$ ls
hello2.txt  hello3.txt  hello.txt

[deploy@sz-local3 test1]$ rm -i hello.txt 
rm: remove regular empty file `hello.txt'? n  #取消删除
[deploy@sz-local3 test1]$ ls
hello2.txt  hello3.txt  hello.txt
[deploy@sz-local3 test1]$ rm -i hello.txt 
rm: remove regular empty file `hello.txt'? y #确认删除
[deploy@sz-local3 test1]$ ls
hello2.txt  hello3.txt
[deploy@sz-local3 test1]$ rm -f hello2.txt 
[deploy@sz-local3 test1]$ ls
hello3.txt
[deploy@sz-local3 test1]$ rm -rf hello3.txt 
[deploy@sz-local3 test1]$ ls
```










