

### Nginx配置文件nginx.conf全解

nginx配置文件nginx.conf的配置http、upstream、server、location等；

nginx负载均衡算法：轮询、加权轮询、ip_hash、url_hash等策略配置；

nginx日志文件access_log配置；

代理服务缓存proxy_buffer设置。

```shell

#user  nobody;   # 用户  用户组  ；一般只有类unix系统有，windows不需要
worker_processes  1;   # 工作进程，一般配置为cpu核心数的2倍

#  错误日志的配置路径
#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;


# 进程id存放路径
#pid        logs/nginx.pid;




# 指定进程可以打开的最大描述符的数量
# 整个系统的最大文件描述符打开数目可以通过命令 ulimit -n  查看
# 所以一般而言，这个值可以是 ulimit -n 除以nginx进程数
#  worker_rlimit_nofile 65535;

events {
    # 使用epoll的I/O 模型；epoll 使用于Linux内核2.6版本及以后的系统
    use epoll;
    # 每一个工作进程的最大连接数量； 理论上而言每一台nginx服务器的最大连接数为： worker_processes*worker_connections
    worker_connections  1024;

    # 超时时间
    #keepalive_timeout 60

    # 客户端请求头部的缓冲区大小，客户端请求一般会小于一页； 可以根据你的系统的分页大小来设定， 命令 getconf PAGESIZE 可以获得当前系统的分页大小（一般4K）
    #client_header_buffer_size 4k;

    # 为打开的文件指定缓存，默认是不启用； max指定缓存数量，建议和打开文件数一致；inactive是指经过这个时间后还没有被请求过则清除该文件的缓存。
    #open_file_cache max=65535 inactive=60s;

    # 多久会检查一次缓存的有效信息
    #open_file_cache_valid 80s;

    # 如果在指定的参数open_file_cache的属性inactive设置的值之内，没有被访问这么多次（open_file_cache_min_uses），则清除缓存
    # 则这里指的是 60s内都没有被访问过一次则清除 的意思
    # open_file_cache_min_uses 1;
}



# 设定http服务；可以利用它的反向代理功能提供负载均衡支持
http {
    #设定mime类型,类型由mime.types文件定义；  可以 cat nginx/conf/mime.types  查看下支持哪些类型
    include       mime.types;
    # 默认mime类型；  application/octet-stream 指的是原始二进制流
    default_type  application/octet-stream;



    # --------------------------------------------------------------------------   #
    # 日志格式设置：
    #   $remote_addr、$http_x_forwarded_for     可以获得客户端ip地址
    #   $remote_user                            可以获得客户端用户名
    #   $time_local                             记录访问的时区以及时间
    #   $request                                请求的url与http协议
    #   $status                                 响应状态成功为200
    #   $body_bytes_sent                        发送给客户端主体内容大小
    #   $http_referer                           记录从哪个页面过来的请求
    #   $http_user_agent                        客户端浏览器信息
    #
    #   注意事项：
    #           通常web服务器(我们的tomcat)放在反向代理(nginx)的后面，这样就不能获取到客户的IP地址了，通过$remote_add拿到的IP地址是反向代理服务器的iP地址。
    #           反向代理服务器(nginx)在转发请求的http头信息中，可以增加$http_x_forwarded_for信息，记录原有客户端的IP地址和原来客户端的请求的服务器地址。
    # --------------------------------------------------------------------------   #
    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    
    #log_format log404 '$status [$time_local] $remote_addr $host$request_uri $sent_http_location';


    # 日志文件；  【注意：】用了log_format则必须指定access_log来指定日志文件
    #access_log  logs/access.log  main;
    #access_log  logs/access.404.log  log404;


    # 保存服务器名字的hash表由 server_names_hash_bucket_size、server_names_hash_max_size 控制
    # server_names_hash_bucket_size 128;

    # 限制通过nginx上传文件的大小
    #client_max_body_size 300m;


    # sendfile 指定 nginx 是否调用sendfile 函数（零拷贝 方式）来输出文件；
    # 对于一般常见应用，必须设为on。
    # 如果用来进行下载等应用磁盘IO重负载应用，可设置为off，以平衡磁盘与网络IO处理速度，降低系统uptime。
    sendfile        on;

    # 此选项允许或禁止使用socke的TCP_CORK的选项，此选项仅在使用sendfile的时候使用；TCP_CORK TCP_NODELAY 选项可以是否打开TCP的内格尔算法
    #tcp_nopush     on;


    #tcp_nodelay on;

    # 后端服务器连接的超时时间_发起握手等候响应超时时间
    #proxy_connect_timeout 90; 

    # 连接成功后_等候后端服务器响应时间_其实已经进入后端的排队之中等候处理（也可以说是后端服务器处理请求的时间）
    #proxy_read_timeout 180;

    # 后端服务器数据回传时间_就是在规定时间之内后端服务器必须传完所有的数据
    #proxy_send_timeout 180;


    # proxy_buffering 这个参数用来控制是否打开后端响应内容的缓冲区，如果这个设置为on，以下的proxy_buffers才生效
    #proxy_buffering on

    # 设置从被代理服务器读取的第一部分应答的缓冲区大小，通常情况下这部分应答中包含一个小的应答头，
    # 默认情况下这个值的大小为指令proxy_buffers中指定的一个缓冲区的大小，不过可以将其设置为更小
    #proxy_buffer_size 256k;

    # 设置用于读取应答（来自被代理服务器--如tomcat）的缓冲区数目和大小，默认情况也为分页大小，根据操作系统的不同可能是4k或者8k
    #proxy_buffers 4 256k;


    # 同一时间处理的请求buffer大小；也可以说是一个最大的限制值--控制同时传输到客户端的buffer大小的。
    #proxy_busy_buffers_size 256k;

    # 设置在写入proxy_temp_path时数据的大小，预防一个工作进程在传递文件时阻塞太长
    #proxy_temp_file_write_size 256k;


    # proxy_temp_path和proxy_cache_path指定的路径必须在同一分区
    #proxy_temp_path /app/tmp/proxy_temp_dir;

    # 设置内存缓存空间大小为200MB，1天没有被访问的内容自动清除，硬盘缓存空间大小为10GB。
    #proxy_cache_path /app/tmp/proxy_cache_dir levels=1:2 keys_zone=cache_one:200m inactive=1d max_size=30g;


    # 如果把它设置为比较大的数值，例如256k，那么，无论使用firefox还是IE浏览器，来提交任意小于256k的图片，都很正常。
    # 如果注释该指令，使用默认的client_body_buffer_size设置，也就是操作系统页面大小的两倍，8k或者16k，问题就出现了。
    # 无论使用firefox4.0还是IE8.0，提交一个比较大，200k左右的图片，都返回500 Internal Server Error错误
    #client_body_buffer_size 512k;   # 默认是页大小的两倍

    # 表示使nginx阻止HTTP应答代码为400或者更高的应答。可以结合error_page指向特定的错误页面展示错误信息
    #proxy_intercept_errors on;

    #keepalive_timeout  0;
    keepalive_timeout  65;

    #gzip  on;



    # 负载均衡 START>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    # upstream 指令定义的节点可以被proxy_pass指令引用；二者结合用来反向代理+负载均衡配置
    # 【内置策略】：轮询、加权轮询、ip_hash、最少连接 默认编译进了nginx
    # 【扩展策略】：fair、通用hash、一致性hash 默认没有编译进nginx
    #-----------------------------------------------------------------------------------------------#
    # 【1】默认是轮询；如果后端服务器down掉，能自动剔除。
    #   upstream bakend {
    #        server 192.168.75.130:8080;
    #        server 192.168.75.132:8080;
    #        server 192.168.75.134:8080;
    #    }
    #
    #【2】权重轮询(加权轮询)：这样配置后，如果总共请求了3次，则前面两次请求到130，后面一次请求到132
    #   upstream bakend {
    #        server 192.168.75.130:8080 weight=2;
    #        server 192.168.75.132:8080 weight=1;
    #   }
    #
    #【3】ip_hash：这种配置会使得每个请求按访问者的ip的hash结果分配，这样每个访客固定访问一个后端服务器，这样也可以解决session的问题。
    #   upstream bakend {
    #        ip_hash;
    #        server 192.168.75.130:8080;
    #        server 192.168.75.132:8080;
    #   }
    #
    #【4】最少连接：将请求分配给连接数最少的服务器。Nginx会统计哪些服务器的连接数最少。
    #   upstream bakend {
    #        least_conn;
    #        server 192.168.75.130:8080;
    #        server 192.168.75.132:8080;
    #   }
    #
    #
    #【5】fair策略(需要安装nginx的第三方模块fair)：按后端服务器的响应时间来分配请求，响应时间短的优先分配。
    #   upstream bakend {
    #        fair;
    #        server 192.168.75.130:8080;
    #        server 192.168.75.132:8080;
    #   }
    #
    #【6】url_hash策略（也是第三方策略）：按访问url的hash结果来分配请求，使每个url定向到同一个后端服务器，后端服务器为缓存时比较有效。
    #               在upstream中加入hash语句，server语句中不能写入weight等其他的参数，hash_method指定hash算法
    #   upstream bakend {
    #        server 192.168.75.130:8080;
    #        server 192.168.75.132:8080;
    #        hash $request_uri;
    #        hash_method crc32;
    #   }
    #
    #【7】其他设置，主要是设备的状态设置
    #   upstream bakend{
    #       ip_hash;
    #       server 127.0.0.1:9090 down;   # down 表示该机器处于下线状态不可用
    #       server 127.0.0.1:8080 weight=2;
    #       server 127.0.0.1:6060;
    #       
    #       # max_fails 默认为1； 最大请求失败的次数，结合fail_timeout使用；
    #       # 以下配置表示 192.168.0.100:8080在处理请求失败3次后，将在15s内不会受到任何请求了
    #       # fail_timeout 默认为10秒。某台Server达到max_fails次失败请求后，在fail_timeout期间内，nginx会认为这台Server暂时不可用，不会将请求分配给它。
    #       server 192.168.0.100:8080 weight=2 max_fails=3 fail_timeout=15;
    #       server 192.168.0.101:8080 weight=3;
    #       server 192.168.0.102:8080 weight=1;
    #       # 限制分配给某台Server处理的最大连接数量，超过这个数量，将不会分配新的连接给它。默认为0，表示不限制。注意：1.5.9之后的版本才有这个配置
    #       server 192.168.0.103:8080 max_conns=1000;
    #       server 127.0.0.1:7070 backup;  # 备份机；其他机器都不可用时，这台机器就上场了
    #       server example.com my_dns_resolve;  # 指定域名解析器；my_dns_resolve需要在http节点配置resolver节点如：resolver 10.0.0.1;
    #   }
    #
    #
    #
    #负载均衡 END<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    #-----------------------------------------------------------------------------------------------#


    ###配置虚拟机START>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    #
    #server{
    #    listen 80;
    #    配置监听端口
    #    server_name image.***.com;
    #    配置访问域名
    #    location ~* \.(mp3|exe)$ {
    #        对以“mp3或exe”结尾的地址进行负载均衡
    #        proxy_pass http://img_relay$request_uri;
    #        设置被代理服务器的端口或套接字，以及URL
    #        proxy_set_header Host $host;
    #        proxy_set_header X-Real-IP $remote_addr;
    #        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    #        以上三行，目的是将代理服务器收到的用户的信息传到真实服务器上
    #    }
    #
    #
    #
    #    location /face {
    #        if ($http_user_agent ~* "xnp") {
    #            rewrite ^(.*)$ http://211.151.188.190:8080/face.jpg redirect;
    #        }
    #        proxy_pass http://img_relay$request_uri;
    #        proxy_set_header Host $host;
    #        proxy_set_header X-Real-IP $remote_addr;
    #        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    #        error_page 404 502 = @fetch;
    #    }
    #
    #    location @fetch {
    #        access_log /data/logs/face.log log404;
    #        rewrite ^(.*)$ http://211.151.188.190:8080/face.jpg redirect;
    #    }
    #
    #    location /image {
    #        if ($http_user_agent ~* "xnp") {
    #            rewrite ^(.*)$ http://211.151.188.190:8080/face.jpg redirect;
    #
    #        }
    #        proxy_pass http://img_relay$request_uri;
    #        proxy_set_header Host $host;
    #        proxy_set_header X-Real-IP $remote_addr;
    #        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    #        error_page 404 502 = @fetch;
    #    }
    #
    #    location @fetch {
    #        access_log /data/logs/image.log log404;
    #        rewrite ^(.*)$ http://211.151.188.190:8080/face.jpg redirect;
    #    }
    #}
    #
    #
    #
    ###其他举例
    #
    #server{
    #
    #    listen 80;
    #
    #    server_name *.***.com *.***.cn;
    #
    #    location ~* \.(mp3|exe)$ {
    #        proxy_pass http://img_relay$request_uri;
    #        proxy_set_header Host $host;
    #        proxy_set_header X-Real-IP $remote_addr;
    #        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    #    }
    #
    #    location / {
    #        if ($http_user_agent ~* "xnp") {
    #            rewrite ^(.*)$ http://i1.***img.com/help/noimg.gif redirect;
    #        }
    #
    #        proxy_pass http://img_relay$request_uri;
    #        proxy_set_header Host $host;
    #        proxy_set_header X-Real-IP $remote_addr;
    #        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    #        #error_page 404 http://i1.***img.com/help/noimg.gif;
    #        error_page 404 502 = @fetch;
    #    }
    #
    #    location @fetch {
    #        access_log /data/logs/baijiaqi.log log404;
    #        rewrite ^(.*)$ http://i1.***img.com/help/noimg.gif redirect;
    #    }
    #}
    #
    #server{
    #    listen 80;
    #    server_name *.***img.com;
    #
    #    location ~* \.(mp3|exe)$ {
    #        proxy_pass http://img_relay$request_uri;
    #        proxy_set_header Host $host;
    #        proxy_set_header X-Real-IP $remote_addr;
    #        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    #    }
    #
    #    location / {
    #        if ($http_user_agent ~* "xnp") {
    #            rewrite ^(.*)$ http://i1.***img.com/help/noimg.gif;
    #        }
    #
    #        proxy_pass http://img_relay$request_uri;
    #        proxy_set_header Host $host;
    #        proxy_set_header X-Real-IP $remote_addr;
    #        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    #        #error_page 404 http://i1.***img.com/help/noimg.gif;
    #        error_page 404 = @fetch;
    #    }
    #
    ##access_log off;
    #
    #    location @fetch {
    #        access_log /data/logs/baijiaqi.log log404;
    #        rewrite ^(.*)$ http://i1.***img.com/help/noimg.gif redirect;
    #    }
    #}
    #
    #server{
    #    listen 8080;
    #    server_name ngx-ha.***img.com;
    #
    #    location / {
    #        stub_status on;
    #        access_log off;
    #    }
    #}
    #
    #server {
    #    listen 80;
    #    server_name imgsrc1.***.net;
    #    root html;
    #}
    #
    #
    #
    #server {
    #    listen 80;
    #    server_name ***.com w.***.com;
    #    # access_log /usr/local/nginx/logs/access_log main;
    #    location / {
    #        rewrite ^(.*)$ http://www.***.com/ ;
    #    }
    #}
    #
    #server {
    #    listen 80;
    #    server_name *******.com w.*******.com;
    #    # access_log /usr/local/nginx/logs/access_log main;
    #    location / {
    #        rewrite ^(.*)$ http://www.*******.com/;
    #    }
    #}
    #
    #server {
    #    listen 80;
    #    server_name ******.com;
    #
    #    # access_log /usr/local/nginx/logs/access_log main;
    #
    #    location / {
    #        rewrite ^(.*)$ http://www.******.com/;
    #    }
    #
    #    location /NginxStatus {
    #        stub_status on;
    #        access_log on;
    #        auth_basic "NginxStatus";
    #        auth_basic_user_file conf/htpasswd;
    #    }
    #
    #    #设定查看Nginx状态的地址
    #    location ~ /\.ht {
    #        deny all;
    #    }#禁止访问.htxxx文件
    #
    #}
    #配置虚拟机END<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            #root   html;
            root   /app;
	    index   zyt505050.html;
	    #index  index.html index.htm;
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

        # proxy the PHP scripts to Apache listening on 127.0.0.1:80
        #
        #location ~ \.php$ {
        #    proxy_pass   http://127.0.0.1;
        #}

        # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
        #
        #location ~ \.php$ {
        #    root           html;
        #    fastcgi_pass   127.0.0.1:9000;
        #    fastcgi_index  index.php;
        #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
        #    include        fastcgi_params;
        #}

        # deny access to .htaccess files, if Apache's document root
        # concurs with nginx's one
        #
        #location ~ /\.ht {
        #    deny  all;
        #}
    }


    # another virtual host using mix of IP-, name-, and port-based configuration
    #
    #server {
    #    listen       8000;
    #    listen       somename:8080;
    #    server_name  somename  alias  another.alias;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}


    # HTTPS server
    #
    #server {
    #    listen       443 ssl;
    #    server_name  localhost;

    #    ssl_certificate      cert.pem;
    #    ssl_certificate_key  cert.key;

    #    ssl_session_cache    shared:SSL:1m;
    #    ssl_session_timeout  5m;

    #    ssl_ciphers  HIGH:!aNULL:!MD5;
    #    ssl_prefer_server_ciphers  on;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}

}

```

