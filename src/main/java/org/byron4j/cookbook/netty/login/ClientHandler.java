package org.byron4j.cookbook.netty.login;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.byron4j.cookbook.netty.apidemo.packet.LoginRequestPacket;
import org.byron4j.cookbook.netty.apidemo.packet.LoginResponsePacket;
import org.byron4j.cookbook.netty.apidemo.packet.Packet;
import org.byron4j.cookbook.netty.apidemo.protocol.PacketCodeC;

import java.util.Date;

/**
 * 消息处理器
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 覆盖方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("客户端开始链结......");
        // 连接连上则，客户端开始发送登录请求

        // 创建登陆对象
        LoginRequestPacket loginRequestPacket = LoginRequestPacket.builder()
                .userId(1)
                .username("Byron")
                .password("123456")
                .build();


        // 编码成ByteBuf
        ByteBuf byteBuf = PacketCodeC.encode(loginRequestPacket);

        // 写数据
        ctx.channel().writeAndFlush(byteBuf);

    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 接收服务端的响应
        ByteBuf byteBuf = (ByteBuf)msg;

        Packet packet = PacketCodeC.decode(byteBuf);

        if(packet instanceof LoginResponsePacket){
            LoginResponsePacket loginResponsePacket = (LoginResponsePacket) packet;
            if(loginResponsePacket.isSuccess() ){
                System.out.println(new Date() + "客户端在服务端成功了，获取到了响应。");
            }else{
                System.out.println(new Date() + "客户端在服务端登陆失败，失败信息：" + loginResponsePacket);
            }

        }else{
            System.err.println(new Date() + "其他的数据对象包类型！");
        }


    }
}
