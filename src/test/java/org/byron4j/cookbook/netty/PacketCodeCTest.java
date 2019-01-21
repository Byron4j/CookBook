package org.byron4j.cookbook.netty;

import io.netty.buffer.ByteBuf;
import org.byron4j.cookbook.netty.apidemo.packet.LoginRequestPacket;
import org.byron4j.cookbook.netty.apidemo.tool.PacketCodeC;
import org.junit.Test;

public class PacketCodeCTest {

    @Test
    public void test(){

        // 协议编码
        ByteBuf byteBuf = PacketCodeC.encode(LoginRequestPacket.builder()
                .userId(1)
                .username("Byron")
                .password("123")
                .build());

        // 协议解码
        LoginRequestPacket loginRequestPacket =  (LoginRequestPacket)PacketCodeC.decode(byteBuf);

        System.out.println("协议解码后的java对象：" + loginRequestPacket);
    }
}
