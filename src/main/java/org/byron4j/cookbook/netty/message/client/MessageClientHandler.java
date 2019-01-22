package org.byron4j.cookbook.netty.message.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.byron4j.cookbook.netty.apidemo.packet.LoginRequestPacket;
import org.byron4j.cookbook.netty.apidemo.packet.LoginResponsePacket;
import org.byron4j.cookbook.netty.apidemo.packet.Packet;
import org.byron4j.cookbook.netty.apidemo.protocol.PacketCodeC;
import org.byron4j.cookbook.netty.message.LoginUtil;
import org.byron4j.cookbook.netty.message.MessageResponsePacket;

import java.util.Date;

public class MessageClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date() + ": 客户端开始准备登陆");

        // 创建登陆对象
        LoginRequestPacket loginRequestPacket = LoginRequestPacket.builder()
                .userId(1)
                .username("Byron")
                .password("123")
                .build();

        // 协议编码
        ByteBuf byteBuf = PacketCodeC.encode(loginRequestPacket);

        // 写数据
        ctx.channel().writeAndFlush(byteBuf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf)msg;

        Packet packet = PacketCodeC.decode(byteBuf);

        if( packet instanceof LoginResponsePacket){
            // 登陆响应
            LoginResponsePacket loginResponsePacket = (LoginResponsePacket)packet;
            if( loginResponsePacket.isSuccess() ){
                System.out.println(new Date() + ": 客户端登录成功");
                // 登陆后绑定已经登录的属性
                LoginUtil.markLogon(ctx.channel());
            }else{
                // 登录失败
                System.out.println(new Date() + ": 登陆失败,原因位:" + loginResponsePacket.getFailReason());
            }
        }else if( packet instanceof MessageResponsePacket){
            // 消息响应
            MessageResponsePacket messageResponsePacket = (MessageResponsePacket)packet;
            System.out.println(new Date() + ": 接收到的响应消息为:" + messageResponsePacket.getMessage());
        }
    }
}
