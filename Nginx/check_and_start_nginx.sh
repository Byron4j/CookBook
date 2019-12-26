#!/bin/bash
# 查看nginx进程是否正在运行，为0则表示已经down掉
nginx_result=$(ps -C nginx --no-heading|wc -l)
echo nginx_result > /app/demo/a.txt
if [ "${nginx_result}" = "0" ]; then
	# 前提需要将nginx设置为service； 或者直接使用nginx所在绝对路径比如： /usr/local/nginx/sbin/nginx
    echo '哈哈哈哈' > /app/demo.a.txt
	service nginx start
    sleep 1
    nginx_result=$(ps -C nginx --no-heading|wc -l)
    if [ "${nginx_result}" = "0" ]; then
		# 如果重启nginx服务还是不行的话，就把keepalived也停止；
		# 这样会访问备keepalived，从而保证主的nginx挂掉备也能使用
        /etc/rc.d/init.d/keepalived stop
    fi
fi
