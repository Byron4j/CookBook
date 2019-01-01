package org.byron4j.cookbook.netty.first;

import java.time.LocalDateTime;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyClient {
	public static void main(String[] args) {
		// 线程组
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		
		// 客户端
		Bootstrap bootstrap = new Bootstrap();
	
		bootstrap
			// 线程模型
			.group(workerGroup)
			// 	IO模型
			.channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<NioSocketChannel>() {

				@Override
				protected void initChannel(NioSocketChannel ch) throws Exception {
					// 通过管道进行通信的
					ch.pipeline().addLast(new LastHandler());
				}
			});
		
		// 连接服务端
		bootstrap.connect("localhost", 8888).addListener(future -> {
			if(future.isSuccess()) {
				System.out.println("连接服务端成功");
			}else {
				// ？ 此处是否实现断线重连功能？
				System.out.println("连接服务端失败");
			}
		});
	}

	public static class LastHandler extends io.netty.channel.ChannelInboundHandlerAdapter{

		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			System.out.println("客户端开始写数据...");
			// 消息通过 ByteBuf 形式进行传播
			ctx.channel().writeAndFlush(getByteBuf(ctx));
		}
		
		private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
			// 通过通道处理上下文分配一个 ByteBuf
			ByteBuf byteBuf = ctx.alloc().buffer();
			byte[] contents = (LocalDateTime.now() + " - Hello,Server!" )
					.getBytes(CharsetUtil.UTF_8);
			System.out.println(LocalDateTime.now() + " - 客户端发送的数据是：" + 
					new String(contents));
			// 将消息写入缓存空间
			byteBuf.writeBytes(contents);
			return byteBuf;
		}
		
	}
}
