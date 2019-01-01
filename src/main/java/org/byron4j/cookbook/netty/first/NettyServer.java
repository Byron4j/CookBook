package org.byron4j.cookbook.netty.first;

import java.time.LocalDateTime;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyServer {
	public static void main(String[] args) {
		// 主线程组
		NioEventLoopGroup masterGroup = new NioEventLoopGroup();
		// 工作线程组
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		
		serverBootstrap
			// 线程模型
			.group(masterGroup, workerGroup)
			// IO模型
			.channel(NioServerSocketChannel.class)
			// 客户端处理逻辑---处理来自客户端的请求
			.childHandler(new ChannelInitializer<NioSocketChannel>() {

				@Override
				protected void initChannel(NioSocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LastHandler());
				}
			});
		
		// 服务端绑定端口并启动
		serverBootstrap.bind(8888);
	}
	
	public static class LastHandler extends io.netty.channel.ChannelInboundHandlerAdapter{

		/**
		 * channelRead() 方法是在数据被接收的时候调用
		 */
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			// Netty 传送是bytebuf对象
			// Netty 里面数据读写是以 ByteBuf 为单位进行交互的
			ByteBuf byteBuf = (ByteBuf)msg;
			System.out.println(LocalDateTime.now() + " - 服务端读到的内容：\"" + 
					byteBuf.toString(CharsetUtil.UTF_8) + "\"");
		}
		
	}
}
