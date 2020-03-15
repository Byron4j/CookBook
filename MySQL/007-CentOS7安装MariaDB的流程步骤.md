# CentOS7安装MariaDB的流程步骤

安装前需要彻底清除你的服务器计算机上原来安装过的mysql、maraidb的文件，不然会出现各种问题。
CentOS7安装MariaDB的流程步骤如下：

====================================================
- 执行安装命令: `yum -y install mariadb*`


	- 注意事项（这一步不是必须的，出现时可以这样尝试着解决）
	*mariadb    错误 [Errno 5] [Errno 12] Cannot allocate memory
	*一般云服务器内存很低，可以设置一下
		```
			vi /etc/my.cnf
			[mysqld]
			innodb_buffer_pool_size=50M
		```

- 开机自动启动MariaDB：`systemctl enable mariadb`

- 启动MariaDB：`systemctl start mariadb`

- 查看MariaDB状态：`systemctl status mariadb`

- 初次安装数据库，进行数据库配置：
	- `mysql_secure_installation`
	- 提示：
		-- 第一次操作直接回车
		-- 第二次Set root password?[Y/n] 输入Y
		-- New password: 输入你的密码
		-- 确认密码
	- Success!

- 登录数据库：`mysql -uroot -p`

- 设置字符集（建议设置为：UTF-8）：	
	- `vim /etc/my.cnf`
	- [mysqld]后面追加以下内容
		```
			init_connect='SET collation_connection = utf8_unicode_ci'
			init_connect='SET NAMES utf8'
			character-set-server=utf8
			collation-server=utf8_unicode_ci
			skip-character-set-client-handshake
		```
- 修改文件：`vim /etc/my.cnf.d/client.cnf`
	- [client] 后面追加以下内容
		```
			default-character-set=utf8
		```
- 修改文件：`vim /etc/my.cnf.d/mysql-clients.cnf`
	- [mysql] 后面追加以下内容
		```
			default-character-set=utf8
		```
- 接着重启服务生效：`systemctl restart mariadb`

- MYSQL客户端进入，查看字符集：
	- 登录进入： 
		- `show variables like "%character%";`
		- `show variables like "%collation%";`
- 如果要开放远程登录，得开放防火墙，允许3306访问：
	- `firewall-cmd --permanent --zone=public --add-port=3306/tcp`
	- `firewall-cmd --reload`
	
- MYSQL客户端进入修改权限，允许远程登录：
	- 【选择使用mysql数据库】`use mysql;`
	- select user,host from user;
	- 【修改权限】`update user set host='%' where host='localhost';`
	- select user,host from user;
	- 【刷新使得权限生效】`flush privileges;`
	
- 测试：在其他计算机远程登录
	
	- `mysql -h域名 -uroot -p`

good luck!
-----
------

你可能遇到的错误：
- CentOS 7 MariaDB Error “Failed to start mariadb.service: Unit not found.” [closed]
-  错误 [Errno 5] [Errno 12] Cannot allocate memory

**解决办法都是**： ==请检查你的计算机是否有残留的mysql或者mariadb版本的文件（特别是一些云服务器初始化时可能自带的相关版本）==

-----

以下是整个安装流程，供参考：

```shell
[app]# yum -y install mariadb*
Loaded plugins: fastestmirror
Repodata is over 2 weeks old. Install yum-cron? Or run: yum makecache fast
base                                                                                             | 3.6 kB  00:00:00     
epel                                                                                             | 5.3 kB  00:00:00     
extras                                                                                           | 2.9 kB  00:00:00     
updates                                                                                          | 2.9 kB  00:00:00     
(1/7): epel/x86_64/group_gz                                                                      |  95 kB  00:00:00     
(2/7): base/7/x86_64/group_gz                                                                    | 165 kB  00:00:00     
(3/7): epel/x86_64/updateinfo                                                                    | 1.0 MB  00:00:00     
(4/7): extras/7/x86_64/primary_db                                                                | 164 kB  00:00:00     
(5/7): epel/x86_64/primary_db                                                                    | 6.7 MB  00:00:00     
(6/7): updates/7/x86_64/primary_db                                                               | 6.7 MB  00:00:00     
(7/7): base/7/x86_64/primary_db                                                                  | 6.0 MB  00:00:00     
Determining fastest mirrors
Resolving Dependencies
--> Running transaction check
---> Package mariadb.x86_64 1:5.5.64-1.el7 will be installed
---> Package mariadb-bench.x86_64 1:5.5.64-1.el7 will be installed
--> Processing Dependency: perl(GD) for package: 1:mariadb-bench-5.5.64-1.el7.x86_64
--> Processing Dependency: perl(Data::Dumper) for package: 1:mariadb-bench-5.5.64-1.el7.x86_64
--> Processing Dependency: perl(DBI) for package: 1:mariadb-bench-5.5.64-1.el7.x86_64
---> Package mariadb-devel.x86_64 1:5.5.64-1.el7 will be installed
---> Package mariadb-embedded.x86_64 1:5.5.64-1.el7 will be installed
--> Processing Dependency: libaio.so.1(LIBAIO_0.4)(64bit) for package: 1:mariadb-embedded-5.5.64-1.el7.x86_64
--> Processing Dependency: libaio.so.1(LIBAIO_0.1)(64bit) for package: 1:mariadb-embedded-5.5.64-1.el7.x86_64
--> Processing Dependency: libaio.so.1()(64bit) for package: 1:mariadb-embedded-5.5.64-1.el7.x86_64
---> Package mariadb-embedded-devel.x86_64 1:5.5.64-1.el7 will be installed
---> Package mariadb-libs.x86_64 1:5.5.52-1.el7 will be updated
---> Package mariadb-libs.x86_64 1:5.5.64-1.el7 will be an update
---> Package mariadb-server.x86_64 1:5.5.64-1.el7 will be installed
--> Processing Dependency: perl-DBD-MySQL for package: 1:mariadb-server-5.5.64-1.el7.x86_64
---> Package mariadb-test.x86_64 1:5.5.64-1.el7 will be installed
--> Processing Dependency: perl(Test::More) for package: 1:mariadb-test-5.5.64-1.el7.x86_64
--> Processing Dependency: perl(Env) for package: 1:mariadb-test-5.5.64-1.el7.x86_64
--> Running transaction check
---> Package libaio.x86_64 0:0.3.109-13.el7 will be installed
---> Package perl-DBD-MySQL.x86_64 0:4.023-6.el7 will be installed
---> Package perl-DBI.x86_64 0:1.627-4.el7 will be installed
--> Processing Dependency: perl(RPC::PlServer) >= 0.2001 for package: perl-DBI-1.627-4.el7.x86_64
--> Processing Dependency: perl(RPC::PlClient) >= 0.2000 for package: perl-DBI-1.627-4.el7.x86_64
---> Package perl-Data-Dumper.x86_64 0:2.145-3.el7 will be installed
---> Package perl-Env.noarch 0:1.04-2.el7 will be installed
---> Package perl-GD.x86_64 0:2.49-3.el7 will be installed
--> Processing Dependency: gd >= 2.0.28 for package: perl-GD-2.49-3.el7.x86_64
--> Processing Dependency: libpng15.so.15()(64bit) for package: perl-GD-2.49-3.el7.x86_64
--> Processing Dependency: libjpeg.so.62()(64bit) for package: perl-GD-2.49-3.el7.x86_64
--> Processing Dependency: libgd.so.2()(64bit) for package: perl-GD-2.49-3.el7.x86_64
--> Processing Dependency: libfontconfig.so.1()(64bit) for package: perl-GD-2.49-3.el7.x86_64
--> Processing Dependency: libXpm.so.4()(64bit) for package: perl-GD-2.49-3.el7.x86_64
--> Processing Dependency: libX11.so.6()(64bit) for package: perl-GD-2.49-3.el7.x86_64
---> Package perl-Test-Simple.noarch 0:0.98-243.el7 will be installed
--> Processing Dependency: perl(Test::Harness) >= 2.03 for package: perl-Test-Simple-0.98-243.el7.noarch
--> Running transaction check
---> Package fontconfig.x86_64 0:2.13.0-4.3.el7 will be installed
--> Processing Dependency: freetype >= 2.8-7 for package: fontconfig-2.13.0-4.3.el7.x86_64
--> Processing Dependency: fontpackages-filesystem for package: fontconfig-2.13.0-4.3.el7.x86_64
--> Processing Dependency: dejavu-sans-fonts for package: fontconfig-2.13.0-4.3.el7.x86_64
---> Package gd.x86_64 0:2.0.35-26.el7 will be installed
---> Package libX11.x86_64 0:1.6.7-2.el7 will be installed
--> Processing Dependency: libX11-common >= 1.6.7-2.el7 for package: libX11-1.6.7-2.el7.x86_64
--> Processing Dependency: libxcb.so.1()(64bit) for package: libX11-1.6.7-2.el7.x86_64
---> Package libXpm.x86_64 0:3.5.12-1.el7 will be installed
---> Package libjpeg-turbo.x86_64 0:1.2.90-8.el7 will be installed
---> Package libpng.x86_64 2:1.5.13-7.el7_2 will be installed
---> Package perl-PlRPC.noarch 0:0.2020-14.el7 will be installed
--> Processing Dependency: perl(Net::Daemon) >= 0.13 for package: perl-PlRPC-0.2020-14.el7.noarch
--> Processing Dependency: perl(Net::Daemon::Test) for package: perl-PlRPC-0.2020-14.el7.noarch
--> Processing Dependency: perl(Net::Daemon::Log) for package: perl-PlRPC-0.2020-14.el7.noarch
--> Processing Dependency: perl(Compress::Zlib) for package: perl-PlRPC-0.2020-14.el7.noarch
---> Package perl-Test-Harness.noarch 0:3.28-3.el7 will be installed
--> Running transaction check
---> Package dejavu-sans-fonts.noarch 0:2.33-6.el7 will be installed
--> Processing Dependency: dejavu-fonts-common = 2.33-6.el7 for package: dejavu-sans-fonts-2.33-6.el7.noarch
---> Package fontpackages-filesystem.noarch 0:1.44-8.el7 will be installed
---> Package freetype.x86_64 0:2.4.11-12.el7 will be updated
---> Package freetype.x86_64 0:2.8-14.el7 will be an update
---> Package libX11-common.noarch 0:1.6.7-2.el7 will be installed
---> Package libxcb.x86_64 0:1.13-1.el7 will be installed
--> Processing Dependency: libXau.so.6()(64bit) for package: libxcb-1.13-1.el7.x86_64
---> Package perl-IO-Compress.noarch 0:2.061-2.el7 will be installed
--> Processing Dependency: perl(Compress::Raw::Zlib) >= 2.061 for package: perl-IO-Compress-2.061-2.el7.noarch
--> Processing Dependency: perl(Compress::Raw::Bzip2) >= 2.061 for package: perl-IO-Compress-2.061-2.el7.noarch
---> Package perl-Net-Daemon.noarch 0:0.48-5.el7 will be installed
--> Running transaction check
---> Package dejavu-fonts-common.noarch 0:2.33-6.el7 will be installed
---> Package libXau.x86_64 0:1.0.8-2.1.el7 will be installed
---> Package perl-Compress-Raw-Bzip2.x86_64 0:2.061-3.el7 will be installed
---> Package perl-Compress-Raw-Zlib.x86_64 1:2.061-4.el7 will be installed
--> Finished Dependency Resolution

Dependencies Resolved

========================================================================================================================
 Package                                Arch                  Version                         Repository           Size
========================================================================================================================
Installing:
 mariadb                                x86_64                1:5.5.64-1.el7                  base                8.7 M
 mariadb-bench                          x86_64                1:5.5.64-1.el7                  base                388 k
 mariadb-devel                          x86_64                1:5.5.64-1.el7                  base                755 k
 mariadb-embedded                       x86_64                1:5.5.64-1.el7                  base                3.6 M
 mariadb-embedded-devel                 x86_64                1:5.5.64-1.el7                  base                7.4 M
 mariadb-server                         x86_64                1:5.5.64-1.el7                  base                 11 M
 mariadb-test                           x86_64                1:5.5.64-1.el7                  base                8.1 M
Updating:
 mariadb-libs                           x86_64                1:5.5.64-1.el7                  base                759 k
Installing for dependencies:
 dejavu-fonts-common                    noarch                2.33-6.el7                      base                 64 k
 dejavu-sans-fonts                      noarch                2.33-6.el7                      base                1.4 M
 fontconfig                             x86_64                2.13.0-4.3.el7                  base                254 k
 fontpackages-filesystem                noarch                1.44-8.el7                      base                9.9 k
 gd                                     x86_64                2.0.35-26.el7                   base                146 k
 libX11                                 x86_64                1.6.7-2.el7                     base                607 k
 libX11-common                          noarch                1.6.7-2.el7                     base                164 k
 libXau                                 x86_64                1.0.8-2.1.el7                   base                 29 k
 libXpm                                 x86_64                3.5.12-1.el7                    base                 55 k
 libaio                                 x86_64                0.3.109-13.el7                  base                 24 k
 libjpeg-turbo                          x86_64                1.2.90-8.el7                    base                135 k
 libpng                                 x86_64                2:1.5.13-7.el7_2                base                213 k
 libxcb                                 x86_64                1.13-1.el7                      base                214 k
 perl-Compress-Raw-Bzip2                x86_64                2.061-3.el7                     base                 32 k
 perl-Compress-Raw-Zlib                 x86_64                1:2.061-4.el7                   base                 57 k
 perl-DBD-MySQL                         x86_64                4.023-6.el7                     base                140 k
 perl-DBI                               x86_64                1.627-4.el7                     base                802 k
 perl-Data-Dumper                       x86_64                2.145-3.el7                     base                 47 k
 perl-Env                               noarch                1.04-2.el7                      base                 16 k
 perl-GD                                x86_64                2.49-3.el7                      base                173 k
 perl-IO-Compress                       noarch                2.061-2.el7                     base                260 k
 perl-Net-Daemon                        noarch                0.48-5.el7                      base                 51 k
 perl-PlRPC                             noarch                0.2020-14.el7                   base                 36 k
 perl-Test-Harness                      noarch                3.28-3.el7                      base                302 k
 perl-Test-Simple                       noarch                0.98-243.el7                    base                170 k
Updating for dependencies:
 freetype                               x86_64                2.8-14.el7                      base                380 k

Transaction Summary
========================================================================================================================
Install  7 Packages (+25 Dependent packages)
Upgrade  1 Package  (+ 1 Dependent package)

Total download size: 47 M
Downloading packages:
Delta RPMs disabled because /usr/bin/applydeltarpm not installed.
(1/34): dejavu-fonts-common-2.33-6.el7.noarch.rpm                                                |  64 kB  00:00:00     
(2/34): fontconfig-2.13.0-4.3.el7.x86_64.rpm                                                     | 254 kB  00:00:00     
(3/34): fontpackages-filesystem-1.44-8.el7.noarch.rpm                                            | 9.9 kB  00:00:00     
(4/34): freetype-2.8-14.el7.x86_64.rpm                                                           | 380 kB  00:00:00     
(5/34): gd-2.0.35-26.el7.x86_64.rpm                                                              | 146 kB  00:00:00     
(6/34): dejavu-sans-fonts-2.33-6.el7.noarch.rpm                                                  | 1.4 MB  00:00:00     
(7/34): libX11-common-1.6.7-2.el7.noarch.rpm                                                     | 164 kB  00:00:00     
(8/34): libX11-1.6.7-2.el7.x86_64.rpm                                                            | 607 kB  00:00:00     
(9/34): libXau-1.0.8-2.1.el7.x86_64.rpm                                                          |  29 kB  00:00:00     
(10/34): libXpm-3.5.12-1.el7.x86_64.rpm                                                          |  55 kB  00:00:00     
(11/34): libaio-0.3.109-13.el7.x86_64.rpm                                                        |  24 kB  00:00:00     
(12/34): libjpeg-turbo-1.2.90-8.el7.x86_64.rpm                                                   | 135 kB  00:00:00     
(13/34): libpng-1.5.13-7.el7_2.x86_64.rpm                                                        | 213 kB  00:00:00     
(14/34): libxcb-1.13-1.el7.x86_64.rpm                                                            | 214 kB  00:00:00     
(15/34): mariadb-bench-5.5.64-1.el7.x86_64.rpm                                                   | 388 kB  00:00:00     
(16/34): mariadb-devel-5.5.64-1.el7.x86_64.rpm                                                   | 755 kB  00:00:00     
(17/34): mariadb-5.5.64-1.el7.x86_64.rpm                                                         | 8.7 MB  00:00:00     
(18/34): mariadb-embedded-5.5.64-1.el7.x86_64.rpm                                                | 3.6 MB  00:00:00     
(19/34): mariadb-libs-5.5.64-1.el7.x86_64.rpm                                                    | 759 kB  00:00:00     
(20/34): mariadb-server-5.5.64-1.el7.x86_64.rpm                                                  |  11 MB  00:00:00     
(21/34): mariadb-test-5.5.64-1.el7.x86_64.rpm                                                    | 8.1 MB  00:00:00     
(22/34): perl-Compress-Raw-Bzip2-2.061-3.el7.x86_64.rpm                                          |  32 kB  00:00:00     
(23/34): perl-Compress-Raw-Zlib-2.061-4.el7.x86_64.rpm                                           |  57 kB  00:00:00     
(24/34): perl-DBD-MySQL-4.023-6.el7.x86_64.rpm                                                   | 140 kB  00:00:00     
(25/34): perl-DBI-1.627-4.el7.x86_64.rpm                                                         | 802 kB  00:00:00     
(26/34): perl-Data-Dumper-2.145-3.el7.x86_64.rpm                                                 |  47 kB  00:00:00     
(27/34): perl-Env-1.04-2.el7.noarch.rpm                                                          |  16 kB  00:00:00     
(28/34): perl-GD-2.49-3.el7.x86_64.rpm                                                           | 173 kB  00:00:00     
(29/34): perl-IO-Compress-2.061-2.el7.noarch.rpm                                                 | 260 kB  00:00:00     
(30/34): perl-Net-Daemon-0.48-5.el7.noarch.rpm                                                   |  51 kB  00:00:00     
(31/34): perl-PlRPC-0.2020-14.el7.noarch.rpm                                                     |  36 kB  00:00:00     
(32/34): perl-Test-Harness-3.28-3.el7.noarch.rpm                                                 | 302 kB  00:00:00     
(33/34): perl-Test-Simple-0.98-243.el7.noarch.rpm                                                | 170 kB  00:00:00     
(34/34): mariadb-embedded-devel-5.5.64-1.el7.x86_64.rpm                                          | 7.4 MB  00:00:00     
------------------------------------------------------------------------------------------------------------------------
Total                                                                                    35 MB/s |  47 MB  00:00:01     
Running transaction check
Running transaction test
Transaction test succeeded
Running transaction
  Installing : perl-Data-Dumper-2.145-3.el7.x86_64                                                                 1/36 
  Updating   : 1:mariadb-libs-5.5.64-1.el7.x86_64                                                                  2/36 
  Installing : 1:mariadb-5.5.64-1.el7.x86_64                                                                       3/36 
  Installing : libaio-0.3.109-13.el7.x86_64                                                                        4/36 
  Installing : 2:libpng-1.5.13-7.el7_2.x86_64                                                                      5/36 
  Updating   : freetype-2.8-14.el7.x86_64                                                                          6/36 
  Installing : libjpeg-turbo-1.2.90-8.el7.x86_64                                                                   7/36 
  Installing : fontpackages-filesystem-1.44-8.el7.noarch                                                           8/36 
  Installing : dejavu-fonts-common-2.33-6.el7.noarch                                                               9/36 
  Installing : dejavu-sans-fonts-2.33-6.el7.noarch                                                                10/36 
  Installing : fontconfig-2.13.0-4.3.el7.x86_64                                                                   11/36 
  Installing : 1:mariadb-embedded-5.5.64-1.el7.x86_64                                                             12/36 
  Installing : 1:mariadb-devel-5.5.64-1.el7.x86_64                                                                13/36 
  Installing : 1:perl-Compress-Raw-Zlib-2.061-4.el7.x86_64                                                        14/36 
  Installing : libXau-1.0.8-2.1.el7.x86_64                                                                        15/36 
  Installing : libxcb-1.13-1.el7.x86_64                                                                           16/36 
  Installing : perl-Net-Daemon-0.48-5.el7.noarch                                                                  17/36 
  Installing : libX11-common-1.6.7-2.el7.noarch                                                                   18/36 
  Installing : libX11-1.6.7-2.el7.x86_64                                                                          19/36 
  Installing : libXpm-3.5.12-1.el7.x86_64                                                                         20/36 
  Installing : gd-2.0.35-26.el7.x86_64                                                                            21/36 
  Installing : perl-GD-2.49-3.el7.x86_64                                                                          22/36 
  Installing : perl-Test-Harness-3.28-3.el7.noarch                                                                23/36 
  Installing : perl-Test-Simple-0.98-243.el7.noarch                                                               24/36 
  Installing : perl-Env-1.04-2.el7.noarch                                                                         25/36 
  Installing : perl-Compress-Raw-Bzip2-2.061-3.el7.x86_64                                                         26/36 
  Installing : perl-IO-Compress-2.061-2.el7.noarch                                                                27/36 
  Installing : perl-PlRPC-0.2020-14.el7.noarch                                                                    28/36 
  Installing : perl-DBI-1.627-4.el7.x86_64                                                                        29/36 
  Installing : perl-DBD-MySQL-4.023-6.el7.x86_64                                                                  30/36 
  Installing : 1:mariadb-server-5.5.64-1.el7.x86_64                                                               31/36 
  Installing : 1:mariadb-test-5.5.64-1.el7.x86_64                                                                 32/36 
  Installing : 1:mariadb-bench-5.5.64-1.el7.x86_64                                                                33/36 
  Installing : 1:mariadb-embedded-devel-5.5.64-1.el7.x86_64                                                       34/36 
  Cleanup    : freetype-2.4.11-12.el7.x86_64                                                                      35/36 
  Cleanup    : 1:mariadb-libs-5.5.52-1.el7.x86_64                                                                 36/36 
  Verifying  : 1:mariadb-libs-5.5.64-1.el7.x86_64                                                                  1/36 
  Verifying  : fontconfig-2.13.0-4.3.el7.x86_64                                                                    2/36 
  Verifying  : perl-IO-Compress-2.061-2.el7.noarch                                                                 3/36 
  Verifying  : 2:libpng-1.5.13-7.el7_2.x86_64                                                                      4/36 
  Verifying  : freetype-2.8-14.el7.x86_64                                                                          5/36 
  Verifying  : perl-Compress-Raw-Bzip2-2.061-3.el7.x86_64                                                          6/36 
  Verifying  : fontpackages-filesystem-1.44-8.el7.noarch                                                           7/36 
  Verifying  : perl-Env-1.04-2.el7.noarch                                                                          8/36 
  Verifying  : 1:mariadb-embedded-5.5.64-1.el7.x86_64                                                              9/36 
  Verifying  : perl-GD-2.49-3.el7.x86_64                                                                          10/36 
  Verifying  : 1:mariadb-devel-5.5.64-1.el7.x86_64                                                                11/36 
  Verifying  : perl-Test-Harness-3.28-3.el7.noarch                                                                12/36 
  Verifying  : 1:mariadb-5.5.64-1.el7.x86_64                                                                      13/36 
  Verifying  : dejavu-fonts-common-2.33-6.el7.noarch                                                              14/36 
  Verifying  : libX11-1.6.7-2.el7.x86_64                                                                          15/36 
  Verifying  : 1:mariadb-test-5.5.64-1.el7.x86_64                                                                 16/36 
  Verifying  : libxcb-1.13-1.el7.x86_64                                                                           17/36 
  Verifying  : perl-DBI-1.627-4.el7.x86_64                                                                        18/36 
  Verifying  : libaio-0.3.109-13.el7.x86_64                                                                       19/36 
  Verifying  : libX11-common-1.6.7-2.el7.noarch                                                                   20/36 
  Verifying  : libXpm-3.5.12-1.el7.x86_64                                                                         21/36 
  Verifying  : libjpeg-turbo-1.2.90-8.el7.x86_64                                                                  22/36 
  Verifying  : perl-Data-Dumper-2.145-3.el7.x86_64                                                                23/36 
  Verifying  : 1:mariadb-bench-5.5.64-1.el7.x86_64                                                                24/36 
  Verifying  : dejavu-sans-fonts-2.33-6.el7.noarch                                                                25/36 
  Verifying  : perl-Test-Simple-0.98-243.el7.noarch                                                               26/36 
  Verifying  : gd-2.0.35-26.el7.x86_64                                                                            27/36 
  Verifying  : perl-Net-Daemon-0.48-5.el7.noarch                                                                  28/36 
  Verifying  : perl-PlRPC-0.2020-14.el7.noarch                                                                    29/36 
  Verifying  : libXau-1.0.8-2.1.el7.x86_64                                                                        30/36 
  Verifying  : 1:mariadb-embedded-devel-5.5.64-1.el7.x86_64                                                       31/36 
  Verifying  : perl-DBD-MySQL-4.023-6.el7.x86_64                                                                  32/36 
  Verifying  : 1:mariadb-server-5.5.64-1.el7.x86_64                                                               33/36 
  Verifying  : 1:perl-Compress-Raw-Zlib-2.061-4.el7.x86_64                                                        34/36 
  Verifying  : 1:mariadb-libs-5.5.52-1.el7.x86_64                                                                 35/36 
  Verifying  : freetype-2.4.11-12.el7.x86_64                                                                      36/36 

Installed:
  mariadb.x86_64 1:5.5.64-1.el7                                 mariadb-bench.x86_64 1:5.5.64-1.el7                    
  mariadb-devel.x86_64 1:5.5.64-1.el7                           mariadb-embedded.x86_64 1:5.5.64-1.el7                 
  mariadb-embedded-devel.x86_64 1:5.5.64-1.el7                  mariadb-server.x86_64 1:5.5.64-1.el7                   
  mariadb-test.x86_64 1:5.5.64-1.el7                           

Dependency Installed:
  dejavu-fonts-common.noarch 0:2.33-6.el7                    dejavu-sans-fonts.noarch 0:2.33-6.el7                      
  fontconfig.x86_64 0:2.13.0-4.3.el7                         fontpackages-filesystem.noarch 0:1.44-8.el7                
  gd.x86_64 0:2.0.35-26.el7                                  libX11.x86_64 0:1.6.7-2.el7                                
  libX11-common.noarch 0:1.6.7-2.el7                         libXau.x86_64 0:1.0.8-2.1.el7                              
  libXpm.x86_64 0:3.5.12-1.el7                               libaio.x86_64 0:0.3.109-13.el7                             
  libjpeg-turbo.x86_64 0:1.2.90-8.el7                        libpng.x86_64 2:1.5.13-7.el7_2                             
  libxcb.x86_64 0:1.13-1.el7                                 perl-Compress-Raw-Bzip2.x86_64 0:2.061-3.el7               
  perl-Compress-Raw-Zlib.x86_64 1:2.061-4.el7                perl-DBD-MySQL.x86_64 0:4.023-6.el7                        
  perl-DBI.x86_64 0:1.627-4.el7                              perl-Data-Dumper.x86_64 0:2.145-3.el7                      
  perl-Env.noarch 0:1.04-2.el7                               perl-GD.x86_64 0:2.49-3.el7                                
  perl-IO-Compress.noarch 0:2.061-2.el7                      perl-Net-Daemon.noarch 0:0.48-5.el7                        
  perl-PlRPC.noarch 0:0.2020-14.el7                          perl-Test-Harness.noarch 0:3.28-3.el7                      
  perl-Test-Simple.noarch 0:0.98-243.el7                    

Updated:
  mariadb-libs.x86_64 1:5.5.64-1.el7                                                                                    

Dependency Updated:
  freetype.x86_64 0:2.8-14.el7                                                                                          

Complete!
[ app]# systemctl enable mariadb
Created symlink from /etc/systemd/system/multi-user.target.wants/mariadb.service to /usr/lib/systemd/system/mariadb.service.
[ app]# systemctl start mariadb
[ app]# systemctl status mariadb
● mariadb.service - MariaDB database server
   Loaded: loaded (/usr/lib/systemd/system/mariadb.service; enabled; vendor preset: disabled)
   Active: active (running) since Sun 2020-03-15 19:23:48 CST; 8s ago
  Process: 4279 ExecStartPost=/usr/libexec/mariadb-wait-ready $MAINPID (code=exited, status=0/SUCCESS)
  Process: 4192 ExecStartPre=/usr/libexec/mariadb-prepare-db-dir %n (code=exited, status=0/SUCCESS)
 Main PID: 4278 (mysqld_safe)
   CGroup: /system.slice/mariadb.service
           ├─4278 /bin/sh /usr/bin/mysqld_safe --basedir=/usr
           └─4442 /usr/libexec/mysqld --basedir=/usr --datadir=/var/lib/mysql --plugin-dir=/usr/lib64/mysql/plugin --...

Mar 15 19:23:46 iZwz9edz6cpxxf0o19aqisZ mariadb-prepare-db-dir[4192]: MySQL manual for more instructions.
Mar 15 19:23:46 iZwz9edz6cpxxf0o19aqisZ mariadb-prepare-db-dir[4192]: Please report any problems at http://mariadb....ra
Mar 15 19:23:46 iZwz9edz6cpxxf0o19aqisZ mariadb-prepare-db-dir[4192]: The latest information about MariaDB is avail.../.
Mar 15 19:23:46 iZwz9edz6cpxxf0o19aqisZ mariadb-prepare-db-dir[4192]: You can find additional information about the...t:
Mar 15 19:23:46 iZwz9edz6cpxxf0o19aqisZ mariadb-prepare-db-dir[4192]: http://dev.mysql.com
Mar 15 19:23:46 iZwz9edz6cpxxf0o19aqisZ mariadb-prepare-db-dir[4192]: Consider joining MariaDB's strong and vibrant...y:
Mar 15 19:23:46 iZwz9edz6cpxxf0o19aqisZ mariadb-prepare-db-dir[4192]: https://mariadb.org/get-involved/
Mar 15 19:23:46 iZwz9edz6cpxxf0o19aqisZ mysqld_safe[4278]: 200315 19:23:46 mysqld_safe Logging to '/var/log/maria...og'.
Mar 15 19:23:46 iZwz9edz6cpxxf0o19aqisZ mysqld_safe[4278]: 200315 19:23:46 mysqld_safe Starting mysqld daemon wit...ysql
Mar 15 19:23:48 iZwz9edz6cpxxf0o19aqisZ systemd[1]: Started MariaDB database server.
Hint: Some lines were ellipsized, use -l to show in full.
[root@iZwz9edz6cpxxf0o19aqisZ app]# mysql_secure_installation

NOTE: RUNNING ALL PARTS OF THIS SCRIPT IS RECOMMENDED FOR ALL MariaDB
      SERVERS IN PRODUCTION USE!  PLEASE READ EACH STEP CAREFULLY!

In order to log into MariaDB to secure it, we'll need the current
password for the root user.  If you've just installed MariaDB, and
you haven't set the root password yet, the password will be blank,
so you should just press enter here.

Enter current password for root (enter for none): 
ERROR 1045 (28000): Access denied for user 'root'@'localhost' (using password: YES)
Enter current password for root (enter for none): 
ERROR 1045 (28000): Access denied for user 'root'@'localhost' (using password: YES)
Enter current password for root (enter for none): 
OK, successfully used password, moving on...

Setting the root password ensures that nobody can log into the MariaDB
root user without the proper authorisation.

Set root password? [Y/n] Y
New password: 
Re-enter new password: 
Password updated successfully!
Reloading privilege tables..
 ... Success!


By default, a MariaDB installation has an anonymous user, allowing anyone
to log into MariaDB without having to have a user account created for
them.  This is intended only for testing, and to make the installation
go a bit smoother.  You should remove them before moving into a
production environment.

Remove anonymous users? [Y/n] n
 ... skipping.

Normally, root should only be allowed to connect from 'localhost'.  This
ensures that someone cannot guess at the root password from the network.

Disallow root login remotely? [Y/n] n
 ... skipping.

By default, MariaDB comes with a database named 'test' that anyone can
access.  This is also intended only for testing, and should be removed
before moving into a production environment.

Remove test database and access to it? [Y/n] n
 ... skipping.

Reloading the privilege tables will ensure that all changes made so far
will take effect immediately.

Reload privilege tables now? [Y/n] Y
 ... Success!

Cleaning up...

All done!  If you've completed all of the above steps, your MariaDB
installation should now be secure.

Thanks for using MariaDB!

```