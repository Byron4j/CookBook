package org.byron4j.cookbook.netty.message.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.byron4j.cookbook.netty.apidemo.packet.LoginRequestPacket;
import org.byron4j.cookbook.netty.apidemo.packet.LoginResponsePacket;
import org.byron4j.cookbook.netty.apidemo.packet.Packet;
import org.byron4j.cookbook.netty.apidemo.protocol.PacketCodeC;
import org.byron4j.cookbook.netty.message.MessageRequestPacket;
import org.byron4j.cookbook.netty.message.MessageResponsePacket;

import java.util.Date;

@SuppressWarnings("all")
public class MessageServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(new Date() + "客户端开始登陆...");

        ByteBuf byteBuf = (ByteBuf)msg;

        // 协议解码
        Packet packet = PacketCodeC.decode(byteBuf);

        if(packet instanceof LoginRequestPacket){
            // 登录请求
            LoginRequestPacket loginRequestPacket = (LoginRequestPacket)packet;

            // 构建响应对象
            LoginResponsePacket loginResponsePacket = LoginResponsePacket.builder().build();

            if(valid(loginRequestPacket) ){
                loginResponsePacket.setSuccess(true);
                loginResponsePacket.setFailCode("000000");
                loginResponsePacket.setFailReason("恭喜，登陆成功！");
                System.out.println(new Date() + "-登陆成功.");
            }else{
                loginResponsePacket.setSuccess(false);
                loginResponsePacket.setFailCode("9999");
                loginResponsePacket.setFailReason("账号密码验证失败.");
                System.out.println(new Date() + "-登陆失败.");

            }

            // 登陆响应
            ByteBuf responseByteBuf = PacketCodeC.encode(loginResponsePacket);
            ctx.channel().writeAndFlush(responseByteBuf);

        }else if( packet instanceof MessageRequestPacket){


            MessageRequestPacket messageRequestPacket = (MessageRequestPacket)packet;
            System.out.println(new Date() + ": 收到客户端的消息:" + messageRequestPacket.getMessage());

            // 准备回复消息
            MessageResponsePacket messageResponsePacket = MessageResponsePacket.builder().message(new Date() + ":【服务端回复】" + messageRequestPacket.getMessage()).build();

            ByteBuf byteBuf1 = PacketCodeC.encode(messageResponsePacket);
            ctx.channel().writeAndFlush(byteBuf1);

        }
    }

    private boolean valid(LoginRequestPacket loginRequestPacket){
        return true;
    }
}
