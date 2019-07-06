package org.byron4j.cookbook.netty.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class TimerClient {
	public static void main(String[] args) {
		int port = 8090;
		
		if(null != args && args.length > 0) {
			port = Integer.valueOf(args[0]);
		}
		
		java.net.Socket socket = null;
		try {
			socket = new java.net.Socket("127.0.0.1", port);
			
			// 获取输入流，接受服务端的响应
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// 获取输出流，发送请求到服务端
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println("Hello, time server! This is my request.");
			
			// readLine 需要读取'\n'
			String response = in.readLine();
			System.out.println("Client receive server's response:" + response);
			
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
}
