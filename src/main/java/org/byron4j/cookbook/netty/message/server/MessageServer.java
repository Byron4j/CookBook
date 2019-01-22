package org.byron4j.cookbook.netty.message.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MessageServer {

    private static final int PORT = 8888;

    public static void main(String[] args){
        NioEventLoopGroup masterGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap
                .group(masterGroup, workerGroup) // 线程模型
                .channel(NioServerSocketChannel.class) // IO 模型
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new MessageServerHandler());
                    }
                });

        bind(serverBootstrap, PORT);
    }

    private static void bind(ServerBootstrap serverBootstrap, final int port){
        serverBootstrap.bind(port).addListener(
                (future) -> {
                    if( future.isSuccess() ){
                        System.out.println("服务端启动成功，绑定端口为:" + port);
                    }else{
                        System.err.println("服务端启动失败，绑定端口为:" + port);
                    }
                }
        );
    }
}
