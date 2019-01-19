## Linux的yum命令

yum(Yellow dog Updater, Modified) 是一个shell前端软件包管理器。
基于RPM包管理，能够从指定的服务器自动下载RPM包并且安装，可以自动处理依赖性关系，并且一次安装所有依赖的软件包，无须繁琐的一次一次下载、安装。
yum提供了查找、安装、删除某个、一组甚至全部软件包的命令，而且命令简洁又好记。

### yum语法

>yum [options] [command] [package...]

- options : 可选项包括 -h 帮助、-y 当安装过程提示选择全部为"yes"、 -q 不显示安装的过程，等等
- command ： 要进行的操作
- package ： 操作的对象

### yum常用命令

- 列出所有可更新的软件清单：**yum check-update**
- 更新所有软件命令： **yum update**
- 安装指定的软件包： **yum install <package_name>**
- 更新指定的软件包： **yum update <package_name>**

![](yumupdate.png)

- 列出所有可安装的软件包： **yum list**
- 删除软件包： **yum remove <package_name>**
- 查找软件包： **yum search <keyword>**
- 清除缓存的固定命令：
    - **yum clean package** ： 清除缓存目录下的软件包
    - **yum clean headers** : 清除缓存目录下的headers
    - **yum clean oldheaders** ： 清除缓存目录下的旧headers
    - **yum clean、yum clean all = yum clean packages;yum clean oldheaders** ： 清除缓存目录下的软件包以及旧的headers

### 安装 pam-devel
```shell
[deploy@sz-local3 lff]$ sudo yum install pam-devel
Loaded plugins: fastestmirror, security
Setting up Install Process
Loading mirror speeds from cached hostfile
 * PUIAS_6_computational: mirror.math.princeton.edu
 * base: mirrors.aliyun.com
 * epel: sg.fedora.ipserverone.com
 * extras: mirrors.aliyun.com
 * updates: mirrors.aliyun.com
Resolving Dependencies
--> Running transaction check
---> Package pam-devel.x86_64 0:1.1.1-24.el6 will be installed
--> Processing Dependency: pam = 1.1.1-24.el6 for package: pam-devel-1.1.1-24.el6.x86_64
--> Running transaction check
---> Package pam.x86_64 0:1.1.1-20.el6 will be updated
---> Package pam.x86_64 0:1.1.1-24.el6 will be an update
--> Finished Dependency Resolution

Dependencies Resolved

===============================================================================================================================================================
 Package                                Arch                                Version                                    Repository                         Size
===============================================================================================================================================================
Installing:
 pam-devel                              x86_64                              1.1.1-24.el6                               base                              205 k
Updating for dependencies:
 pam                                    x86_64                              1.1.1-24.el6                               base                              659 k

Transaction Summary
===============================================================================================================================================================
Install       1 Package(s)
Upgrade       1 Package(s)

Total download size: 865 k
Is this ok [y/N]: 
```

### 移除 pam-devel

```shell
[deploy@sz-local3 lff]$ sudo yum remove pam-devel
Loaded plugins: fastestmirror, security
Setting up Remove Process
Resolving Dependencies
--> Running transaction check
---> Package pam-devel.x86_64 0:1.1.1-24.el6 will be erased
--> Finished Dependency Resolution

Dependencies Resolved

===============================================================================================================================================================
 Package                                Arch                                Version                                   Repository                          Size
===============================================================================================================================================================
Removing:
 pam-devel                              x86_64                              1.1.1-24.el6                              @base                              547 k

Transaction Summary
===============================================================================================================================================================
Remove        1 Package(s)

Installed size: 547 k
Is this ok [y/N]: 
```

### 利用 yum 的功能，找出以 pam 开头的软件包

```shell
[deploy@sz-local3 lff]$ yum list pam*
Loaded plugins: fastestmirror, security
Loading mirror speeds from cached hostfile
 * PUIAS_6_computational: www.puias.princeton.edu
 * base: mirrors.aliyun.com
 * epel: sg.fedora.ipserverone.com
 * extras: mirrors.aliyun.com
 * updates: mirrors.aliyun.com
Installed Packages
pam.x86_64                                                     1.1.1-24.el6                                            @base                                   
pam-devel.x86_64                                               1.1.1-24.el6                                            @base                                   
pam_krb5.x86_64                                                2.3.11-9.el6                                            @anaconda-CentOS-201410241409.x86_64/6.6
pam_passwdqc.x86_64                                            1.0.5-6.el6                                             @anaconda-CentOS-201410241409.x86_64/6.6
Available Packages
pam.i686                                                       1.1.1-24.el6                                            base                                    
pam-devel.i686                                                 1.1.1-24.el6                                            base                                    
pam_afs_session.i686                                           2.5-3.el6                                               epel                                    
pam_afs_session.x86_64                                         2.5-3.el6                                               epel                                    
pam_krb5.i686                                                  2.3.11-9.el6                                            base                                    
pam_ldap.i686                                                  185-11.el6                                              base                                    
pam_ldap.x86_64                                                185-11.el6                                              base                                    
pam_mapi.i686                                                  0.3.4-1.el6                                             epel                                    
pam_mapi.x86_64                                           
```

### 国内 yum 源

网易63的yum源是国内最好的yum源之一。

#### 安装步骤

- 1. 首先备份 /etc/yum.repos.d/CentOS-Base.repo

```shell
mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup
```

- 2.下载对应版本 repo 文件, 放入 /etc/yum.repos.d/ (操作前请做好相应备份):

- **CentOS5** ：http://mirrors.163.com/.help/CentOS5-Base-163.repo
- **CentOS6** ：http://mirrors.163.com/.help/CentOS6-Base-163.repo
- **CentOS7** ：http://mirrors.163.com/.help/CentOS7-Base-163.repo

```shell
wget http://mirrors.163.com/.help/CentOS6-Base-163.repo  # 下载
mv CentOS6-Base-163.repo CentOS-Base.repo # 重命名
```

- 3. 运行以下命令生成缓存

```shell
yum clean all
yum makecache
```

















