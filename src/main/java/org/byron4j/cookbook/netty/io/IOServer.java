package org.byron4j.cookbook.netty.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * 演示传统IO的Socket服务端
 */
public class IOServer {
    public static void main(String args[]) throws IOException {
    		// 创建服务端，监听本地800端口
        ServerSocket serverSocket = new ServerSocket(8000);
        System.out.println("服务端启动，等待client连接.");
        new Thread(() -> {
	        	while(true) {
	        		try {
		        		// 死循环，监听客户端连接; 该方法是一个阻塞方法
	            		Socket socket = serverSocket.accept();
	            		System.out.println("新连接加入...");
	            		// 来一个客户端则，新建一个线程负责处理该客户端的请求
		        		new Thread(() -> {
			                	try {
				                		// 获取客户端输入流
				                		java.io.InputStream isInputStream = socket.getInputStream();
				                		byte[] bytebuf = new byte[1024];
				                		int len = 0;
				                		while( (len = isInputStream.read(bytebuf) )!= -1) {
				                			// 如果客户端输入流不为空，则输出其传送的内容
				                			System.out.println("接收到client的消息：" + new String(bytebuf, 0, len));
				                		}
								} catch (Exception e) {
									// TODO: handle exception
								}
		        		}).start();
	        		} catch (Exception e) {
						System.err.println(Thread.currentThread().getName() + "发生异常：" + e);
				}
	        }
        }).start(); ;
    }
}


