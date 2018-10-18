

##Windows环境Nginx

### [下载Nginx](http://nginx.org/en/download.html) 

[官网下载需要的版本](http://nginx.org/en/download.html)

### 运行Nginx

执行nginx.exe，如果运行成功，并且在安装目录下的logs目录下生成了日志文件

- access.log ： 访问日志
- error.log ： 错误日志
- nginx.pid ： nginx进程id号


### 查看Nginx进程

在命令行运行 **tasklist** 查看nginx进程：

```batch
E:\111softwares\nginx-1.15.5> tasklist /fi "imagename eq nginx.exe"

PS E:\111softwares\nginx-1.15.5> tasklist /fi "imagename eq nginx.exe"

映像名称                       PID 会话名              会话#       内存使用
========================= ======== ================ =========== ============
nginx.exe                    40776 Console                    1      7,464 K
nginx.exe                    63844 Console                    1      7,416 K
nginx.exe                    33868 Console                    1      7,432 K
nginx.exe                    64936 Console                    1      7,708 K
nginx.exe                    35716 Console                    1      7,128 K
nginx.exe                    62632 Console                    1      7,428 K
nginx.exe                    44196 Console                    1      7,180 K
nginx.exe                    63348 Console                    1      7,436 K
nginx.exe                    33440 Console                    1      7,188 K
nginx.exe                    52872 Console                    1      7,424 K
```

其中一个进程是主master进程，其他的是工作worker进程。 如果nginx灭有启动成功，可以查看 log\error.log 错误日志文件。 如果日志文件没有创建成功，可能在Windows事件日志中找到原因。如果展示了一个错误页面而不是期望中的页面，也可以查看 logs\error.log 错误日志。

在配置中使用文件目录需要符合 UNIX风格：

>access_log   logs/site.log;
>
>root         C:/web/html;

### 管理 Nginx

nginx 以一个标准的控制台应用而不是注册为一个服务运行，可以使用如下命令进行管理：

- nginx -s stop : 快速关闭
- nginx -s quit ： 优雅关闭
- nginx -s reload ： 改变配置时，会使用新的配置信息开启一个新的worker进程，并优雅的关闭旧的进程
- nginx -s reopen ： 重新打开日志文件log files


### windowx版本的Nginx使用注意事项

- 尽管可能会启动好几个worker进程， 但只有一个worker可以处理任何work
- 一个worker最多处理1024个并发连接
- 不支持UDP协议功能

### windows版本nginx未来增强功能

- 可以作为一个服务启动
- 使用 I/O 端口作为连接处理方法
- 在单个worker进程中可以使用多个worker线程工作