package org.byron4j.cookbook.netty.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.sound.midi.Soundbank;



public class TimerServer {
	public static void main(String[] args) {
		int port = 8090;
		
		if(null != args && args.length > 0) {
			port = Integer.valueOf(args[0]);
		}
		
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(port);
			
			System.out.println("Timer server is start in port:" + port);
			
			Socket socket = null;
			while (true) {
				socket = serverSocket.accept();
				
				// 开启一个新的线程处理客户端连接
				new Thread(new TimeServerHandler(socket)).start();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static class TimeServerHandler implements Runnable{

		private Socket socket;
		
		
		public TimeServerHandler(Socket socket) {
			super();
			this.socket = socket;
		}


		/**
		 * 接收客户端消息并响应 
		 */
		@Override
		public void run() {
			try {
				// 获取客户端输入流
				BufferedReader in = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				
				while(true) {
					String content = in.readLine();
					if(content == null) {
						break;
					}
					
					System.out.println("The time server receive context:" + content);
					
					// 响应当前时间给到客户端
					out.println(LocalDate.now() + " " + LocalTime.now());
					out.flush();
					System.out.println("over.");
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}
}
