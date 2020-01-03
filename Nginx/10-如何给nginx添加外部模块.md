# 如何给nginx添加外部模块

以第三方openresty提供的模块：echo-nginx-module 为例，演示如何给已经安装好的nginx添加外部模块。



## 前提介绍



测试机的nginx目录在：  `/usr/local/nginx`

使用`ls` 可以查看该目录下的内容如下：

```shell
client_body_temp  html  nginx-1.13.9         proxy_temp  scgi_temp
conf              fastcgi_temp       logs  nginx-1.13.9.tar.gz  sbin        uwsgi_temp
```



## 下载第三方模块

进入  `/usr/local/nginx`，目录操作。可以使用命令下载：

```shell
wget  https://github.com/openresty/echo-nginx-module/archive/v0.61.tar.gz
```



## 解压+重命名

下载后会保存到一个 v0.61 的目录，其实是 `v0.61.tar.gz`，可以使用以下命令解压：

```shell
tar -zxvf v0.61
```

解压后会看到一个目录 `echo-nginx-module-0.61` 生成了，可以将其重命名一下：

```shell
mv echo-nginx-module-0.61 echo-nginx-module
```



## 重新编译+add新模块

因为已经安装好了nginx， 所以只需要重新编译添加模块即可，以下是详细操作步骤。

### 切换目录

```shell
cd /usr/local/nginx/nginx-1.13.9
```

### 使用命令`--add-module`添加模块

```shell
./configure --prefix=/usr/local/nginx --add-module=/usr/local/nginx/echo-nginx-module
```

### make生成可执行的二进制文件nginx

```shell
make
```

到此为止，会在目录 `/usr/local/nginx/nginx-1.13.9/objs`  生成一个可执行的二进制文件 **nginx**。



> tips
>
> > 对于已经安装好的nginx，只需要make即可； make后会在objs目录生成一个新的nginx可执行文件，替换原来的即可（记得备份一下）



## 备份

备份原来的可执行nginx文件：

```shell
mv /usr/local/nginx/sbin/nginx /usr/local/nginx/sbin/nginx.back
```

## 覆盖

使用新的覆盖旧的可执行二进制文件：

```shell
cp /usr/local/nginx/nginx-1.13.9/objs/nginx /usr/local/nginx/sbin/nginx
```

## 修改配置文件nginx.conf

```shell
location / {
    root   html;
    index  index.html index.htm;
    # mime类型改成这个，不然默认的是二进制流；会变成文件下载的方式...
    default_type    text/plain;
    echo "This is echo module:192.168.75.134";
}
```

## 启动测试

重新启动nginx服务，然后浏览器访问测试，会在浏览器显示：

This is echo module:192.168.75.134