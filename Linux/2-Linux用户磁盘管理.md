## Linux 用户和用户组管理

Linux 系统是一个多用户任务的分时操作系统，任何一个要使用系统资源的用户，都必须首先向系统管理员申请一个账号，然后以这个账号的身份进入系统。
用户的账号一方面可以帮助系统管理员对使用系统的用户进行跟踪，并控制它们对系统资源的访问；另一方面也可以帮助用户组织文件，并为用户提供安全性保护。
每个用户都拥有一个唯一的用户名和各自的口令。

实现用户账户的管理，要完成的工作主要有如下几个方面：
- 用户账号的添加、删除、修改
- 用户口令的管理
- 用户组的管理

### Linux 系统用户账号的管理

用户账号的管理工作主要涉及用户账号的添加、删除、修改
添加用户账号就是在系统中创建一个新账号，然后为新账号分配用户号、用户组、主目录、登陆shell等资源。
刚添加的账号是被锁定的，无法使用。

#### 添加新的用户账号使用 useradd 命令

>useradd 选项 用户名

参数说明：
**选项**：
- -c comment : 指定一段注释性描述
- -d 目录 ： 指定用户主目录，如果此目录不存在，则同时使用 -m 选项，可以创建主目录
- -g 用户组 ： 指定用户所属的用户组
- -G 用户组 ： 用户组指定用户所属的附加组
- -s shell文件 ： 指定用户的登陆shell
- -u 用户号 ： 指定用户的用户号，如果同时有 -o 选项，则可以重复使用其他用户的标识符

**用户名**：
指定新账号的登录名。

```shell
# 此命令创建了一个用户sam，其中-d和-m选项用来为登录名sam产生一个主目录 /usr/sam(/usr为默认的用户主目录所在的父目录)
useradd -d /usr/sam -m sam
```

另一个实例：
```shell
useradd -s /bin/sh -g group –G adm,root gem
```
该命令创建了一个用户gem，该用户的登陆shell是 /bin/sh，它属于group用户组，同时又属于adm、root用户组。
其中group用户组是其主组。这里可能会新建组 ```groupadd group 以及 groupadd adm```。
增加用户账号就是在 /etc/passwd 文件中为新用户增加一条记录，同时更新其他系统文件 如 /etc/shadow、/etc/group 等。
Linux 提供了集成的系统管理工具 userconf，它可以用来对用户账号进行统一管理。

#### 删除账号

如果一个用户的帐号不再使用，可以从系统中删除。删除用户账号就是要将 /etc/passwd 等文件中的该用户的记录删除，必要时还删除用户的主目录。
删除一个用户账号使用 userdel 命令：
>userdel 选项 用户名

常用的选项是 -r， 它的作用是把用户的主目录一起删除。

例如：
```shell
userdel -r sam
```

此命令删除用户sam在系统文件中（主要是 /etc/passwd、/etc/shadow、/etc/group 等）的记录，同时删除用户的主目录。

#### 修改账号

修改用户账号就是根据实际情况更改用户的有关属性，如用户号、主目录、用户组、登陆shell等。
修改已有用户的信息使用usermod命令，其格式如下：
>usermod 选项 用户名

常用的选项和 useradd 类似。-c、-d、-g、-G、-s、-u、-o等。
```shell
usermod -s /bin/ksh -d /home/z –g developer sam
```
此命令将用户sam的登陆shell改为ksh，主目录改为/home/z，用户组改为 developer

#### 用户口令的管理

用户账号刚创建的时候没有口令，但是被系统锁定，无法使用，必须为其指定口令后才可以使用，即使是指定空口令。
指定和修改用户口令的shell命令是passwd。
超级用户可以为自己和其他用户指定口令，普通用户只能用它修改自己的口令。
>passwd 选项 用户名

选项参数：
- -l : 锁定口令，即禁用账号
- -u ： 口令解锁
- -d ： 使账号无口令
- -f ： 强迫用户下次登陆时修改口令

假设当前用户是sanm，则下面命令可以修改用户自己的口令：
```shell
$ passwd 
Old password:****** 
New password:******* 
Re-enter new password:*******
```

如果是超级用户，可以用下列形式制定任何用户的口令：
```shell
# passwd sam 
New password:******* 
Re-enter new password:*******
```

普通用户只能修改自己的口令，修改时会先询问原口令。
超级用户为用户指定口令时，就不需要直到原口令。
为用户指定空口令时，执行以下形式的命令：
>passwd -d sam

此命令将用户sam口令删除，下一次登陆时，系统就不再询问口令。
passwd 开可以使用-l锁定某个用户，使其不能登录：
>passwd -l sam

### Linux 系统用户组的管理

每个用户都有一个用户组，系统可以对一个用户组的所有用户进行集中管理。不同Linux系统对用户组的规定有所不同，如Linux下的用户属于与他同名的用户组，这个用户组在创建用户时同时创建。
用户组的管理涉及添加、删除、修改，实际就是对 /etc/group 文件的更新。

#### 添加用户组 groupadd

>groupadd 选项 用户组

选项参数：
- -g GID: 指定新用户组的组标识符
- -o ： 一般与-g选项同时使用，表示新用户组的GID可以与系统已有用户组的GID相同

实例：
```shell
groupadd group1
``` 
向系统增加一个新用户组group1，新组的组标识符是在当前已有的最大组标识符的基础上加1.
还可以显示指定新组标识符：
```shell
groupadd -g 101 group2
```

#### groupdel 删除用户组

>groupdel 选项 用户组

选项参数：
- -g GID : 为用户组指定新的组标识符
- -o ： 与-g选项同时使用，用户组的新GID可以与系统已有用户组的GID相同
- -n 新用户组 ： 将用户组名改为新名字

```shell
# 从系统中删除用户组group1
groupdel group 1
```

#### 修改用户组 groupmod

>groupmod 选项 用户组

选项参数：
- -g GID : 为用户组指定新的组标识符
- -o ： 与-g选项同时使用
- -n 新用户组： 将用户组的名字改为新名字

```shell
groupmod -g 102 group2
``` 
此命令将组group2的组标识号修改为102.

```shell
groupmod –g 10000 -n group3 group2
```
此命令将组group2的标识号改为10000，组名修改为group3.

#### 如果用户属于多个组，可以在组之间切换，以便具有其他用户组的权限

用户可以在登陆后使用 **newgrp** 切换到其他组：
>newgrp root

将用户组切换至root组。

### 与用户账号有关的系统文件

与用户和用户组相关的信息都存放在一些系统文件中，这些文件包括/etc/passwd, /etc/shadow, /etc/group等。

#### /etc/passwd 文件是用户管理工作涉及的最重要的一个文件

Linux 每个用户都在 /etc/passwd 文件中有一个对应的记录行，记录了这个用户的一些基本属性。
这个文件对于所有用户都是可读的。

查看该文件：
```shell
[deploy@sz-local3 ~]$ cat /etc/passwd
root:x:0:0:root:/root:/bin/bash
bin:x:1:1:bin:/bin:/sbin/nologin
daemon:x:2:2:daemon:/sbin:/sbin/nologin
adm:x:3:4:adm:/var/adm:/sbin/nologin
lp:x:4:7:lp:/var/spool/lpd:/sbin/nologin
sync:x:5:0:sync:/sbin:/bin/sync
shutdown:x:6:0:shutdown:/sbin:/sbin/shutdown
halt:x:7:0:halt:/sbin:/sbin/halt
mail:x:8:12:mail:/var/spool/mail:/sbin/nologin
uucp:x:10:14:uucp:/var/spool/uucp:/sbin/nologin
operator:x:11:0:operator:/root:/sbin/nologin
games:x:12:100:games:/usr/games:/sbin/nologin
gopher:x:13:30:gopher:/var/gopher:/sbin/nologin
ftp:x:14:50:FTP User:/var/ftp:/sbin/nologin
nobody:x:99:99:Nobody:/:/sbin/nologin
dbus:x:81:81:System message bus:/:/sbin/nologin
vcsa:x:69:69:virtual console memory owner:/dev:/sbin/nologin
rpc:x:32:32:Rpcbind Daemon:/var/cache/rpcbind:/sbin/nologin
abrt:x:173:173::/etc/abrt:/sbin/nologin
rpcuser:x:29:29:RPC Service User:/var/lib/nfs:/sbin/nologin
nfsnobody:x:65534:65534:Anonymous NFS User:/var/lib/nfs:/sbin/nologin
haldaemon:x:68:68:HAL daemon:/:/sbin/nologin
ntp:x:38:38::/etc/ntp:/sbin/nologin
saslauth:x:499:76:Saslauthd user:/var/empty/saslauth:/sbin/nologin
postfix:x:89:89::/var/spool/postfix:/sbin/nologin
sshd:x:74:74:Privilege-separated SSH:/var/empty/sshd:/sbin/nologin
oprofile:x:16:16:Special user account to be used by OProfile:/home/oprofile:/sbin/nologin
tcpdump:x:72:72::/:/sbin/nologin
deploy:x:1022:1022:CRF admin,,,:/home/deploy:/bin/bash
www-data:x:1023:1023::/var/www/:/bin/sh
loguser:x:1024:1024::/home/loguser:/bin/bash
```


```shell
root:x:0:0:root:/root:/bin/bash
loguser:x:1024:1024::/home/loguser:/bin/bash
```

/etc/passwd 中一行记录对应着一个用户，每行记录又被冒号(:)分隔为7个字段，格式与具体含义如下：

> <font color=red >用户名:口令:用户标志号:组标志号:注释性描述:主目录:登陆shell</font>

##### 1."用户名" 是代表用户账户的字符串

通常长度不超过8个字符，并且由大小写字母或数字组成，。登陆名中不能有冒号(:)，因为冒号在这里是分隔符。

##### 2."口令" 一些系统中，存放着加密后的用户口令字

虽然这个字段存放的只是用户口令的加密串，不是明文，但是由于 /etc/passwd 文件对所有用户都可读，所以这仍是一个安全隐患。
因此，现在许多 Linux 系统都使用了 shadow 技术，把真正的加密后的用户口令存放到 /etc/shadow 文件中，而在 /etc/passwd 文件中的口令字段中只存放一个特殊的字符，例如 "x" 或者 "*"。

##### 3."用户标志号" 是一个整数，系统内部用它来标志用户

一把情况下它与用户名是一一对应的，如果几个用户名对应的用户标志是一样的，系统内部把它们视为同一个用户，但是它们可以有不同的口令，不同的主目录以及不同的登陆shell等。
通常用户标志符取值范围是 0-65535。0是超级用户的标志，1-99由系统保留，作为管理账号。普通用户的标志号从100开始。

##### 4."组标志号" 字段记录的是用户所属的用户组

它对应着 /etc/group 文件中的一条记录。

##### 5."注释性描述" 字段记录着用户的一些个人情况

例如用户的真实姓名、电话、地址等，这个字段并没有什么实际用途。在不同 Linux 系统中，这个字段并没有统一。
这个字段存放的是一段任意的注释性描述文字，用作finger命令的输出。

##### 6."主目录" 也就是用户的起使工作目录

它是用户在登录到系统之后所处的目录。在大多数系统中，各用户的主目录都被组织在同一个特定的目录下，而用户主目录的名称就是该用户的登录名。
各用户对自己的主目录有读、写、执行（搜索）权限，其他用户对此目录的访问权限则根据具体情况设置。

##### 7."登陆shell" 

用户登陆后，要启动一个进程，负责将用户的操作传给内核，这个进程是用户登陆到系统后运行的命令解释器或某个特定的程序，即shell。
shell 是用户与Linux系统之间的接口。
Linux的shell有许多种，每种都有不同的特点。常用的有sh(Bourne Shell), csh(C Shell), ksh(Korn Shell), tcsh(TENEX/TOPS-20 type C Shell), bash(Bourne Again Shell)等。

系统管理员可以根据情况和用户习惯为用户指定某个shell。如果不指定shell，那么系统使用sh为默认的登陆shell，即这个字段的值为 /bin/sh。

用户的登陆shell也可以指定为某个特定的程序（此程序不是一个命令解释器）。
利用这一点，可以限制用户只能运行指定的应用程序，在该应用程序运行结束后，用户就自动退出了系统。有些Linux系统要求只有那些在系统中登陆了的程序才能出现在这个字段中。

##### 8.系统中有一类用户称为伪用户(pseudo users)

这些用户在 /etc/passwd 文件中也占有一条记录，但是不能登陆，因为他们的登陆shell为空。
它们的存在主要是方便系统管理，满足相应的系统进程对文件属主的要求。

常见的伪用户如下所示：
```shell
伪 用 户 含 义 
bin 拥有可执行的用户命令文件 
sys 拥有系统文件 
adm 拥有帐户文件 
uucp UUCP使用 
lp lp或lpd子系统使用 
nobody NFS使用
```

### 拥有账号文件

##### 1.除了前面列出的伪用户之外，还有许多标准的伪用户，例如：audit、cron、mail、usenet等，它们也都各自为相关的进程和文件所需要

由于 /etc/passwd 文件是所有用户都可读的，如皋用户的密码太简单或者规律较明显的话，容易破解，因此都将加密后的口林分离出来，单独存放在一个文件中，这个文件是 /etc/shadow文件，只有超级用户才有读权限。

##### 2./etc/shadow 中的记录行与/etc/passwd中的一一对应，它由<font color=red>pwconv</font>命令根据/etc/passwd中的数据自动产生

pwconv ：password convert，密码转换的意思。

/etc/shadow 文件格式与/etc/passwd 类似：
>登录名:加密口令:最后一次修改时间:最小时间间隔:最大时间间隔:警告时间:不活动时间:失效时间:标志

- 登录名和/etc/passwd文件的登录名一致
- "口令"字段存放的是加密后的用户口令，长度为13个字符，如果为空，对应用户没有口令，登陆时不需要口令，如果含有不属于集合{./0-9A-Za-z}中的字符，则对应的用户不能登陆
- "最后一次修改时间"表示从某个时间开始到用户最后一次修改口令时的天数。不同系统时间起点可能不一样，一般为1970年1月1日
- "最小时间间隔"指的是两次修改口令之间所需的最小天数
- "最大间隔时间"指的是口令保持有效的最大天数
- "警告时间"表示从系统开始警告用户到用户密码正式失效之间的天数
- "不活动时间"表示用户没有登陆活动但账号仍能保持有效的最大天数
- "失效时间"给出的是一个绝对天数，期满后该账号不再是一个合法的账号，不能再用来登陆了

```shell
＃ cat /etc/shadow

root:Dnakfw28zf38w:8764:0:168:7:::
daemon:*::0:0::::
bin:*::0:0::::
sys:*::0:0::::
adm:*::0:0::::
uucp:*::0:0::::
nuucp:*::0:0::::
auth:*::0:0::::
cron:*::0:0::::
listen:*::0:0::::
lp:*::0:0::::
sam:EkdiSECLWPdSa:9740:0:0::::
```

##### 用户组信息存放在/etc/group文件中

将用户分组是Linux系统中对用户进行管理以及控制访问权限的一种手段。
每个用户都属于某个组；
一个组中可以有多个用户，一个用户也可以属于多个组；
当一个用户是多个组成员时，在/etc/passwd文件中记录的是用户所属主组，也就是登陆默认所属组，而其他组称为附加组。
用户需要访问附加组的文件时，需要使用 **newgrp** 命令切换组。
用户组所有信息存放在/etc/passwd文件中：

>组名:口令:组标识符:组内用户列表

- "组名" 是用户的组名
- "口令" 存放用户组加密后的口令字，一般Linux系统的用户组都没有口令，即这个字段一般为空或者是*
- "组标识符" 与用户标识符，也是一个整数，被系统内部用来标志组
- "组内用户列表" 是属于这个组的所有用户的列表，不同用户用逗号分隔

```shell
root:x:0:
bin:x:1:bin,daemon
daemon:x:2:bin,daemon
sys:x:3:bin,adm
adm:x:4:adm,daemon
tty:x:5:
disk:x:6:
lp:x:7:daemon
mem:x:8:
kmem:x:9:
wheel:x:10:
mail:x:12:mail,postfix
uucp:x:14:
man:x:15:
```

### 添加批量用户

- 1.先编辑一个文本用户文件

按 /etc/passwd 密码文件的格式书写，要注意每个用户的用户名、UID、宿主目录都不可以相同，其中密码栏可以留作空白或输入x符。

示例user.txt内容如下：
```shell
user001::600:100:user:/home/user001:/bin/bash
user002::601:100:user:/home/user002:/bin/bash
user003::602:100:user:/home/user003:/bin/bash
user004::603:100:user:/home/user004:/bin/bash
user005::604:100:user:/home/user005:/bin/bash
user006::605:100:user:/home/user006:/bin/bash
```

- 2.以root身份执行命令/usr/sbin/newusers，从用户文件导入数据

>newusers < user.txt

然后执行命令 vipw 或 vi /etc/passwd 检查 /etc/passwd 文件是否已经出现这些用户的数据，并且用户的宿主目录是否已经创建。

- 3.执行命令 /usr/sbin/pwunconv

将/etc/shadow产生的shadow密码解码。
然后回写到/etc/passwd中，并将/etc/shadow的shadow密码栏删掉，这是为了方便下一步密码转换工作，即先取消shadow password 功能。

>pwunconv

- 4.编辑每个用户的密码对照文件

示例passwd.txt文件：
```shell
user001:密码
user002:密码
user003:密码
user004:密码
user005:密码
user006:密码
```

- 5.以root身份执行/usr/sbin/chpasswd

创建用户密码，chpasswd会经过/usr/bin/passwd命令编码过的密码写入/etc/passwd密码栏。
>chpasswd < passwd.txt

- 6.确定密码经编码写入/etc/passwd的密码栏后

执行命令/usr/sbin/pwconv将密码编码为shadow password，并将结果写入/etc/shadow。
>pwconv

这样就完成了大量用户的创建，之后可以到/home下检查用户宿主目录的权限设置是否都正确，并登陆验证用户密码是否正确。



## Linux 磁盘管理

Linux 磁盘管理常用的三个命令为 df、du、fdisk。

- df ： 列出文件系统的整体磁盘使用量
- du ： 检查磁盘空间使用量
- fdisk ： 用于磁盘分区

#### df 列出文件系统整体磁盘使用量

>df [-ahikHTm] [目录或文件名]

选项参数：
- -a : 列出所有的文件系统，包括系统特有的/proc等文件系统
- -k ： 以KB容量显示文件系统
- -m ： 以MB容量显示文件系统
- -h ： 以人们较为容易阅读的DB、MB、KB等格式自行显示 （常用）
- -H ： 以M=1000K取代M=1024K的进位方式显示
- -T ： 显示文件系统类型，连同该partion的filesystem名称（例如ext3）也列出
- -i ： 不用硬盘容量，而以inode的数量来显示

```shell
[deploy@sz-local3 ~]$ df -h
Filesystem      Size  Used Avail Use% Mounted on
/dev/xvda3       74G   13G   58G  18% /
tmpfs           3.9G   12K  3.9G   1% /dev/shm
/dev/xvda1      976M   27M  898M   3% /boot
```

将系统所有特殊文件格式及名称都列出来：
```shell
[deploy@sz-local3 ~]$ df -aT
Filesystem     Type        1K-blocks     Used Available Use% Mounted on
/dev/xvda3     ext4         77277192 12828924  60516160  18% /
proc           proc                0        0         0    - /proc
sysfs          sysfs               0        0         0    - /sys
devpts         devpts              0        0         0    - /dev/pts
tmpfs          tmpfs         4014352       12   4014340   1% /dev/shm
/dev/xvda1     ext4           999320    27368    919524   3% /boot
none           binfmt_misc         0        0         0    - /proc/sys/fs/binfmt_misc
```

查看/etc目录下的磁盘容量使用情况：
```shell
[deploy@sz-local3 ~]$ df -h /etc
Filesystem      Size  Used Avail Use% Mounted on
/dev/xvda3       74G   13G   58G  18% /
```

#### du 对文件或目录查看其磁盘空间使用情况

>du [-ahsSkm] 文件或目录对象

选项参数：
- -a ：列出所有的文件与目录容量，因为默认仅统计目录底下的文件量而已。
- -h ：以人们较易读的容量格式 (G/M) 显示；
- -s ：列出总量而已，而不列出每个各别的目录占用容量；
- -S ：不包括子目录下的总计，与 -s 有点差别。
- -k ：以 KBytes 列出容量显示；
- -m ：以 MBytes 列出容量显示；

#### fdisk 是Linux的分区表操作工具

>fdisk [-l] 装置名称

- -l ：输出后面接的装置所有的分区内容。若仅有 fdisk -l 时， 则系统将会把整个系统内能够搜寻到的装置的分区均列出来。


