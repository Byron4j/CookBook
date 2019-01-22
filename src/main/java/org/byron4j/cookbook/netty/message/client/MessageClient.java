package org.byron4j.cookbook.netty.message.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.byron4j.cookbook.netty.apidemo.protocol.PacketCodeC;
import org.byron4j.cookbook.netty.message.MessageRequestPacket;

import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MessageClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8888;
    private static final int MAX_RETRY_TIMES = 5; // 客户端连接重试最大次数

    public static void main(String[] args){
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MessageClientHandler());
                    }
                });

        // 连接服务端
        connect(bootstrap, HOST, PORT, MAX_RETRY_TIMES);
    }

    /**
     *
     * @param bootstrap
     * @param host 服务端ip
     * @param port 端口
     * @param maxRetryTimes 最大重试次数
     */
    private static void connect(Bootstrap bootstrap, String host, int port, int maxRetryTimes){
        bootstrap.connect(host, port).addListener(
                new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        // 监听连接状态
                        if( future.isSuccess() ){
                            // 连接成功，则发送给消息
                            System.out.println(new Date() + ": 连接服务端成功，启动控制台线程...");
                            Channel channel = ((ChannelFuture)future).channel();
                            // 客户端处理逻辑
                            startConsoleThread(channel);
                        }else if( 0 == maxRetryTimes){
                            System.err.println("重试最大次数:" + MAX_RETRY_TIMES + ", 放弃连接.");
                        }else{
                            // 重连
                            // 第多少次连接
                            int cnt = (MAX_RETRY_TIMES - maxRetryTimes) + 1;

                            // 次幂时间重连，2、4、8、...
                            int delay = 1 << cnt;
                            System.err.println(new Date() + ": 连接失败，第" + cnt + "次连接...");

                            // 定时重连
                            bootstrap.config().group().schedule(() -> {
                                connect(bootstrap, HOST, PORT, maxRetryTimes - 1);
                            },delay, TimeUnit.SECONDS);
                        }
                    }
                }
        );
    }

    /**
     * 启动控制台线程，用于处理向服务端发送消息
     * @param channel
     */
    private static void startConsoleThread(Channel channel){
        new Thread(
                () -> {
                    // 线程处理逻辑
                    while( !Thread.interrupted() ){
                        if( true ){
                            System.out.println("请输入消息发送至服务端:");
                            Scanner scanner = new Scanner(System.in);
                            String line = scanner.nextLine();

                            MessageRequestPacket messageRequestPacket = MessageRequestPacket.builder().build();
                            messageRequestPacket.setMessage(line);
                            ByteBuf byteBuf = PacketCodeC.encode(messageRequestPacket);
                            channel.writeAndFlush(byteBuf);
                        }
                    }
                }
        ).start();
    }
}
