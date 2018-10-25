## CentOS 安装Nginx

```shell
cd nginx-1.6.2/
```


```shell
./configure


开始检查安装配置信息，这里使用指定模块安装：
 ./configure --prefix=/usr/local/webserver/nginx --with-http_stub_status_module --with-http_ssl_module

checking for OS
 + Linux 3.10.0-514.el7.x86_64 x86_64
checking for C compiler ... found
 + using GNU C compiler
 + gcc version: 4.8.5 20150623 (Red Hat 4.8.5-28) (GCC) 
checking for gcc -pipe switch ... found
checking for gcc builtin atomic operations ... found
checking for C99 variadic macros ... found
checking for gcc variadic macros ... found
checking for unistd.h ... found
checking for inttypes.h ... found
checking for limits.h ... found
checking for sys/filio.h ... not found
checking for sys/param.h ... found
checking for sys/mount.h ... found
checking for sys/statvfs.h ... found
checking for crypt.h ... found
checking for Linux specific features
checking for epoll ... found
checking for EPOLLRDHUP ... found
checking for O_PATH ... found
checking for sendfile() ... found
checking for sendfile64() ... found
checking for sys/prctl.h ... found
checking for prctl(PR_SET_DUMPABLE) ... found
checking for sched_setaffinity() ... found
checking for crypt_r() ... found
checking for sys/vfs.h ... found
checking for nobody group ... found
checking for poll() ... found
checking for /dev/poll ... not found
checking for kqueue ... not found
checking for crypt() ... not found
checking for crypt() in libcrypt ... found
checking for F_READAHEAD ... not found
checking for posix_fadvise() ... found
checking for O_DIRECT ... found
checking for F_NOCACHE ... not found
checking for directio() ... not found
checking for statfs() ... found
checking for statvfs() ... found
checking for dlopen() ... not found
checking for dlopen() in libdl ... found
checking for sched_yield() ... found
checking for SO_SETFIB ... not found
checking for SO_ACCEPTFILTER ... not found
checking for TCP_DEFER_ACCEPT ... found
checking for TCP_KEEPIDLE ... found
checking for TCP_FASTOPEN ... found
checking for TCP_INFO ... found
checking for accept4() ... found
checking for int size ... 4 bytes
checking for long size ... 8 bytes
checking for long long size ... 8 bytes
checking for void * size ... 8 bytes
checking for uint64_t ... found
checking for sig_atomic_t ... found
checking for sig_atomic_t size ... 4 bytes
checking for socklen_t ... found
checking for in_addr_t ... found
checking for in_port_t ... found
checking for rlim_t ... found
checking for uintptr_t ... uintptr_t found
checking for system byte ordering ... little endian
checking for size_t size ... 8 bytes
checking for off_t size ... 8 bytes
checking for time_t size ... 8 bytes
checking for setproctitle() ... not found
checking for pread() ... found
checking for pwrite() ... found
checking for sys_nerr ... found
checking for localtime_r() ... found
checking for posix_memalign() ... found
checking for memalign() ... found
checking for mmap(MAP_ANON|MAP_SHARED) ... found
checking for mmap("/dev/zero", MAP_SHARED) ... found
checking for System V shared memory ... found
checking for POSIX semaphores ... not found
checking for POSIX semaphores in libpthread ... found
checking for struct msghdr.msg_control ... found
checking for ioctl(FIONBIO) ... found
checking for struct tm.tm_gmtoff ... found
checking for struct dirent.d_namlen ... not found
checking for struct dirent.d_type ... found
checking for sysconf(_SC_NPROCESSORS_ONLN) ... found
checking for openat(), fstatat() ... found
checking for getaddrinfo() ... found
checking for PCRE library ... found
checking for PCRE JIT support ... found
checking for OpenSSL library ... found
checking for zlib library ... found
creating objs/Makefile

Configuration summary
  + using system PCRE library
  + using system OpenSSL library
  + md5: using OpenSSL library
  + sha1: using OpenSSL library
  + using system zlib library

  nginx path prefix: "/usr/local/webserver/nginx"
  nginx binary file: "/usr/local/webserver/nginx/sbin/nginx"
  nginx configuration prefix: "/usr/local/webserver/nginx/conf"
  nginx configuration file: "/usr/local/webserver/nginx/conf/nginx.conf"
  nginx pid file: "/usr/local/webserver/nginx/logs/nginx.pid"
  nginx error log file: "/usr/local/webserver/nginx/logs/error.log"
  nginx http access log file: "/usr/local/webserver/nginx/logs/access.log"
  nginx http client request body temporary files: "client_body_temp"
  nginx http proxy temporary files: "proxy_temp"
  nginx http fastcgi temporary files: "fastcgi_temp"
  nginx http uwsgi temporary files: "uwsgi_temp"
  nginx http scgi temporary files: "scgi_temp"

```


使用make会进行安装环境的准备工作包括编译等，这时候还没有安装。

```shell
make
```


make install 安装。

```
make install

[root@test nginx-1.6.2]# make install
make -f objs/Makefile install
make[1]: Entering directory `/data/app/nginx-1.6.2'
test -d '/usr/local/webserver/nginx' || mkdir -p '/usr/local/webserver/nginx'
test -d '/usr/local/webserver/nginx/sbin'               || mkdir -p '/usr/local/webserver/nginx/sbin'
test ! -f '/usr/local/webserver/nginx/sbin/nginx'               || mv '/usr/local/webserver/nginx/sbin/nginx'                   '/usr/local/webserver/nginx/sbin/nginx.old'
cp objs/nginx '/usr/local/webserver/nginx/sbin/nginx'
test -d '/usr/local/webserver/nginx/conf'               || mkdir -p '/usr/local/webserver/nginx/conf'
cp conf/koi-win '/usr/local/webserver/nginx/conf'
cp conf/koi-utf '/usr/local/webserver/nginx/conf'
cp conf/win-utf '/usr/local/webserver/nginx/conf'
test -f '/usr/local/webserver/nginx/conf/mime.types'            || cp conf/mime.types '/usr/local/webserver/nginx/conf'
cp conf/mime.types '/usr/local/webserver/nginx/conf/mime.types.default'
test -f '/usr/local/webserver/nginx/conf/fastcgi_params'                || cp conf/fastcgi_params '/usr/local/webserver/nginx/conf'
cp conf/fastcgi_params          '/usr/local/webserver/nginx/conf/fastcgi_params.default'
test -f '/usr/local/webserver/nginx/conf/fastcgi.conf'          || cp conf/fastcgi.conf '/usr/local/webserver/nginx/conf'
cp conf/fastcgi.conf '/usr/local/webserver/nginx/conf/fastcgi.conf.default'
test -f '/usr/local/webserver/nginx/conf/uwsgi_params'          || cp conf/uwsgi_params '/usr/local/webserver/nginx/conf'
cp conf/uwsgi_params            '/usr/local/webserver/nginx/conf/uwsgi_params.default'
test -f '/usr/local/webserver/nginx/conf/scgi_params'           || cp conf/scgi_params '/usr/local/webserver/nginx/conf'
cp conf/scgi_params             '/usr/local/webserver/nginx/conf/scgi_params.default'
test -f '/usr/local/webserver/nginx/conf/nginx.conf'            || cp conf/nginx.conf '/usr/local/webserver/nginx/conf/nginx.conf'
cp conf/nginx.conf '/usr/local/webserver/nginx/conf/nginx.conf.default'
test -d '/usr/local/webserver/nginx/logs'               || mkdir -p '/usr/local/webserver/nginx/logs'
test -d '/usr/local/webserver/nginx/logs' ||            mkdir -p '/usr/local/webserver/nginx/logs'
test -d '/usr/local/webserver/nginx/html'               || cp -R html '/usr/local/webserver/nginx'
test -d '/usr/local/webserver/nginx/logs' ||            mkdir -p '/usr/local/webserver/nginx/logs'
make[1]: Leaving directory `/data/app/nginx-1.6.2'
```

查看版本；

```shell
[root@test nginx-1.6.2]# /usr/local/webserver/nginx/sbin/nginx -v
nginx version: nginx/1.6.2
```

