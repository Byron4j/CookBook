

# Linux环境下的各种常用开发软件安装教程

## 前提技能准备-防火墙设置

### CentOS6及以下设置：

```shell
vi /etc/sysconfig/iptables
```

添加内容

-A INPUT -m state --state NEW -m tcp -p tcp --dport 你要开放的端口 -j ACCEPT

如，开放8080端口：

```shell
-A INPUT -m state --state NEW -m tcp -p tcp --dport 8080 -j ACCEPT
```



### CentOS7设置：

```shell
	firewall-cmd --zone=public --add-port=8080/tcp --permanent
	systemctl restart firewalld.service
	

说明：	
	--zone=public：表示作用域为公共的；

	--add-port=8080/tcp：添加tcp协议的端口8080；

	--permanent：永久生效，如果没有此参数，则只能维持当前服务生命周期内，重新启动后失效；
```





# 安装JDK

1. 下载jdk 

   http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

2. 从windows上传到linux

   在工具Secure CRT下, 快捷键==Alt+P 会打开一个sftp传输窗口，直接将windows的文件拖拽进去即可完成上传了。==

   或者还用Xftp软件也可以。

   - sftp一些基本语法【有兴趣也可以了解下】：

     ```
     sftp一些基本语法：
     登录远程主机：  sftp username@remote_hostname_or_IP
     查询帮助手册：	 help
     在命令前面加一个！表示命令在本地主机上执行：   
                     //在远程主机上执行
                     vim test.sh
                     //在本地主机上执行
                     !vim test.sh
     从远程主机下载文件:
     				//下载到本机主机当前目录，并且文件名与remoteFile相同
                     get remoteFile
     
                     //下载到本机主机当前目录，并且文件名改为localFile
                     get remoteFile localFile
     从远程主机下载一个目录及其内容:
     				get -r someDirectory
     上传文件到远程主机的当前目录：
     				put localFile
     上传目录到远程主机的当前目录：
     				put -r localDirectory
     退出sftp：
     				exit
     ```

     

3. 检查系统上是否安装了jdk(若安装了就需要先卸载再使用我们自己的)

   ```
   java -version 
   ```

4. 查看出安装的java的软件包

   ```
   rpm -qa | grep java
   ```

5. 卸载linux自带的旧的jdk

   ```
   rpm -e --nodeps java-1.6.0-openjdk-1.6.0.0-1.66.1.13.0.el6.i686
   rpm -e --nodeps java-1.7.0-openjdk-1.7.0.45-2.4.3.3.el6.i686 tzdata-java-2013g-1.el6.noarch
   ```

6. 在 /usr/local 新建一个目录 java

   ```
   mkdir /usr/local/java
   ```

7. 移动 jdk.....gz 到 /usr/local/java下

   ```
   mv jdk-8u181-linux-i586.tar.gz /usr/local/java
   ```

8. 进入 /usr/local/java 目录,解压jdk

   ```
   cd /usr/local/java 
   tar -xvf  jdk-8u181-linux-i586.tar.gz
   ```

9. 配置==环境变量==;  Linux环境变量是以冒号:分隔开的

   ```
   vi /etc/profile
   
   #在该文件的最后面追加如下代码
   export JAVA_HOME=/usr/local/java/jdk1.8.0_181   #填你的目录（你下载的的jdk版本号的目录）
   # Linux环境变量冒号:分隔开
   export PATH=$JAVA_HOME/bin:$PATH  
   ```

10. 保存退出；重新加载配置文件; 否则需要重新连接才生效。

```
source /etc/profile
```



# 安装Tomcat

### 

1. 下载tomcat，这里我们使用tomcat8；  https://tomcat.apache.org/download-80.cgi

2. 上传到linux

3. 在 /usr/local 新建一个文件夹tomcat

   ```shell
   mkdir /usr/local/tomcat
   ```

4. 移动 tomcat...tar.gz 到 /usr/local/tomcat

   ```shell
   mv apache-tomcat-8.5.32.tar.gz /usr/local/tomcat/
   ```

5. 进入/usr/local/tomcat目录,解压Tomcat

   ```shell
   cd /usr/local/tomcat
   tar -xvf apache-tomcat-8.5.32.tar.gz
   ```

6. 进入 /usr/local/tomcat/apache-tomcat-8.5.32/bin

   ```shell
   cd /usr/local/tomcat/apache-tomcat-8.5.32/bin
   ```

7. 启动tomcat

   ```shell
   ./startup.sh
   ```

8. 放开防火墙端口8080

   ```bash
   修改配置文件
   vi /etc/sysconfig/iptables
   增加一行内容
   -A INPUT -m state --state NEW -m tcp -p tcp --dport 8080 -j ACCEPT
   重启加载防火墙或者重启防火墙
   service iptables reload  
   或者
   service iptables restart
   
   ```

9. 测试访问在浏览器输入 http://ip:8080

# 安装MySQL

1. 下载mysql

2. 上传到linux  ==在CRT下,按Alt+P==：会打开一个sftp传输窗口, 输入 ==`put`== 表示将本地文件上传到远程机器；或者使用sftp语法命令如下：

   ```shell
   sftp> put D:\softwares\01_linux-softwares\MySQL-5.5.49-1.linux2.6.i386.rpm-bundle.tar
   Uploading MySQL-5.5.49-1.linux2.6.i386.rpm-bundle.tar to /root/MySQL-5.5.49-1.linux2.6.i386.rpm-bundle.tar
     100% 971KB    971KB/s 00:00:00     
   
   ```

   

3. 检查系统上是否安装了mysql( 若安装了就需要先卸载再使用我们自己的)

   ```
   rpm -qa |grep -i mysql                          #查看
   rpm -e --nodeps mysql-libs-5.1.71-1.el6.i686    #卸载
   ```

4. 在 /usr/local 新建一个文件夹mysql

   ```
   mkdir /usr/local/mysql
   ```

5. 把mysql压缩包移动 到/usr/local/mysql

   ```
   mv MySQL-5.5.49-1.linux2.6.i386.rpm-bundle.tar /usr/local/mysql/
   ```

6. 进入 /usr/local/mysql,解包mysql

   ```
   cd /usr/local/mysql
   tar -xvf MySQL-5.5.49-1.linux2.6.i386.rpm-bundle.tar
   ```

7. 安装 **服务器端**

   ```
   rpm -ivh MySQL-server-5.5.49-1.linux2.6.i386.rpm 
   ```

8. 安装 **客户端**

   ```
   rpm -ivh MySQL-client-5.5.49-1.linux2.6.i386.rpm 
   ```

9. 启动Mysql

   ```
   service mysql start  #启动mysql
   ```

10. 修改密码 为root

```
  /usr/bin/mysqladmin -u root password 'root'
```

11. 登录mysql测试

    ```
     mysql -uroot -proot
    ```

12. 放行3306端口号

```shell
1.修改配置文件
    vi /etc/sysconfig/iptables
    增加一行内容
    -A INPUT -m state --state NEW -m tcp -p tcp --dport 3306 -j ACCEPT
2.重启加载防火墙或者重启防火墙
    service iptables reload  
    或者
    service iptables restart
```

13. ==允许外部通过远程连接 mysql==,需要进入MySQL进行设置。

```
首先需要登录进入mysql，然后利用该账户创建外界可以登陆的账号：
  1.    创建远程账号(账号为root、密码也被identified设置为root)
      	create user 'root'@'%' identified by 'root';  
  2.    授权（授予所有权限）
      	grant all on *.* to 'root'@'%' with grant option;
  3.    使得权限及时生效
      	flush privileges;
```

14. 外界可以通过测试访问该linux的ip:3306来连接数据库实例了

# 安装Nginx

这里我们选择源码安装方式。

##### 

1. 进入http://nginx.org/网站，下载nginx-XXXXX.tar.gz文件

2. 把安装包上传到Linux

3. 在 usr/local下新建文件夹 nginx

```
mkdir /usr/local/nginx
```

4. 将root下的nginx移动到 /usr/local/nginx 

```
mv nginx-1.13.9.tar.gz /usr/local/nginx/
```

5. 进入/usr/local/nginx, 解包

```
cd /usr/local/nginx/
tar -xvf nginx-1.13.9.tar.gz
```

6. 源码安装Nginx需要依赖环境gcc  

   Nginx是C/C++语言开发，安装Nginx需要先将官网下载的源码进行编译，编译依赖gcc环境，所以需要安装gcc。

```
yum -y install gcc-c++ 
```

7. 安装Nginx依赖环境pcre（pcre内存管理）、zlib（nginx使用的压缩包库）、openssl（ssl支持）

```
yum -y install pcre pcre-devel
yum -y install zlib zlib-devel
yum -y install openssl openssl-devel                            
```

8. 编译和安装nginx

```
cd nginx-1.13.9     进入nginx目录
./configure         配置nginx(在nginx-1.13.9目录中执行这个配置文件)
make             	编译nginx
make install        安装nginx
```

9. 进去sbin目录,启动

```
cd   /usr/local/nginx/sbin   进入/usr/local/nginx/sbin这个目录
./nginx         			 启动Nginx
```

10. 放行端口80

```
修改配置文件
		vi /etc/sysconfig/iptables
		增加一行内容
		-A INPUT -m state --state NEW -m tcp -p tcp --dport 80 -j ACCEPT
重启加载防火墙或者重启防火墙
      	service iptables reload  
      	或者
      	service iptables restart
```

10. 停止Nginx服务器

```
cd   /usr/local/nginx/sbin           进入/usr/local/nginx/sbin这个目录
./nginx   -s   stop                  停止Nginx
```

11. 测试访问，启动后可以通过ip访问了



**还可以将nginx注册为service，使用service nginx start|stop|restart来使用，可以参考：https://blog.csdn.net/zixiao217/article/details/103726501**

# 安装Redis

1. 安装gcc-c++环境

```bash
yum -y install gcc-c++
```

2. 下载Redis
3. 上传到Linux
4. 解压

```
tar -zxf redis-5.0.7.tar.gz
```

5. 编译

```
make
```

6. 安装到指定路径，因为redis默认会安装到 /usr/local/bin  这个目录下去；所以我们特意指定一下目录到/usr/local/redis  目录去。

```
# 指定安装路径
make install PREFIX=/usr/local/redis  

# 如果看到了这个：  Hint: It's a good idea to run 'make test' ;)
# 不要担心，这只是一个提示信息不影响
```

7. 进入安装好的redis目录,复制配置文件

```
cd /usr/local/redis/bin
# 把配置文件拷贝到redis命令所在的目录，便于后续操作
cp /usr/local/redis/redis-5.0.7/redis.conf ./
```

8. 修改配置文件

```bash
# 修改配置文件
vi redis.conf
# 允许Redis以后台方式启动，这样只要不断电就可以一直运行
#修改 daemonize 为 yes，默认为no
daemonize yes
# Redis服务器可以跨网络访问
#修改 bind 为 0.0.0.0，默认为当前主机可以访问127.0.0.1
bind 0.0.0.0
# 开启aof持久化方式
appendonly yes
# 当然还可以增加一些其他的可选项，随意
```

9. 指定配置文件启动redis

```bash
./redis-server redis.conf
```

10. 放行6379端口

```shell
修改配置文件
		vi /etc/sysconfig/iptables
		增加一行内容
		-A INPUT -m state --state NEW -m tcp -p tcp --dport 6379 -j ACCEPT
重启加载防火墙或者重启防火墙
      	service iptables reload  
      	或者
      	service iptables restart
```

11. 访问测试，redis默认端口6379