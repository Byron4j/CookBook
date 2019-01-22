package org.byron4j.cookbook.netty.login;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.byron4j.cookbook.netty.apidemo.packet.LoginRequestPacket;
import org.byron4j.cookbook.netty.apidemo.packet.LoginResponsePacket;
import org.byron4j.cookbook.netty.apidemo.packet.Packet;
import org.byron4j.cookbook.netty.apidemo.tool.PacketCodeC;

import javax.sound.midi.SoundbankResource;
import java.util.Date;

/**
 * 服务端处理逻辑器
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
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

        }
    }

    private boolean valid(LoginRequestPacket loginRequestPacket){
        return true;
    }
}










