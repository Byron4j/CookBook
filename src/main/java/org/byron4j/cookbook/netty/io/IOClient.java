package org.byron4j.cookbook.netty.io;

import java.net.Socket;
import java.time.LocalDateTime;

public class IOClient {
	public static void main(String[] args) {
		new Thread(() -> {
			try {
				// 创建连接本地、端口为8000的客户端
				Socket socket = new Socket("localhost", 8000);
				while(true) {
					socket.getOutputStream().write((LocalDateTime.now() + " - Hello,this is client. - " + Thread.currentThread().getName()).getBytes());
					Thread.sleep(500);
				}
			} catch (Exception e) {
				System.err.println(Thread.currentThread().getName() + "-客户端发生错误：" + e);
			}
		}).start();;
		
	}
}
