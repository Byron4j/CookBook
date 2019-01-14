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



























