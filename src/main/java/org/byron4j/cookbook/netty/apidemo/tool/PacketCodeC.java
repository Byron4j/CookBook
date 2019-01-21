package org.byron4j.cookbook.netty.apidemo.tool;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.byron4j.cookbook.netty.apidemo.command.Command;
import org.byron4j.cookbook.netty.apidemo.packet.LoginRequestPacket;
import org.byron4j.cookbook.netty.apidemo.packet.Packet;
import org.byron4j.cookbook.netty.apidemo.serializer.JSONSerializer;
import org.byron4j.cookbook.netty.apidemo.serializer.Serializer;
import org.byron4j.cookbook.netty.apidemo.serializer.SerializerAlgorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装成协议二进制的过程
 */
public class PacketCodeC {

    /**
     * 魔数
     */
    private static final int MAGIC_NUMBER = 0x12345678;

    /**
     * Java对象容器
     */
    private static final Map<Byte, Class<? extends Packet>> packetMap;

    /**
     * 序列化器容器
     */
    private static final Map<Byte, Serializer> serializerMap;

    /**
     * 初始化资源
     */
    static{
        packetMap = new HashMap();
        packetMap.put(Command.LOGIN_REQUEST, LoginRequestPacket.class);

        serializerMap = new HashMap<>();
        serializerMap.put(SerializerAlgorithm.JSON, new JSONSerializer());


    }
    /**
     * 完成协议编码过程，得到Netty传输对象ByteBuf
     * @param packet
     * @return
     */
    public static ByteBuf encode(Packet packet){

        // 1.创建ByteBuf对象
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();

        // 2.序列化java对象
        byte[] bytes = Serializer.DEFAULT.serialzer(packet);

        // 3.协议编码
        byteBuf.writeInt(MAGIC_NUMBER); // 魔数
        byteBuf.writeByte(packet.getVersion()); // 版本号
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm()); // 序列化算法
        byteBuf.writeByte(packet.getCommand());  // 指令
        byteBuf.writeInt(bytes.length); // 数据大小
        byteBuf.writeBytes(bytes); // 数据内容

        return byteBuf;
    }


    /**
     * 完成协议解码的过程
     * @param byteBuf
     * @return
     */
    public static Packet decode(ByteBuf byteBuf){

        // 跳过协议的魔数
        byteBuf.skipBytes(4);

        //跳过版本号
        byteBuf.skipBytes(1);

        // 序列化算法
        byte serializerAlgorithm = byteBuf.readByte();

        // 指令
        byte command = byteBuf.readByte();

        // 数据包长度
        int length = byteBuf.readInt();


        // 缓存数据的数组
        byte[] bytes = new byte[length];
        // 读取数据包到bytes中
        byteBuf.readBytes(bytes);

        // 根据指令类型获取数据对象类型
        Class<? extends Packet> requestType = getRequestType(command);
        // 根据序列化算法获取序列化器
        Serializer serializer = getSerializer(serializerAlgorithm);

        // 反序列化成对象
        if( requestType != null && serializer != null ){
            return serializer.deserialzer(requestType, bytes);
        }

        return null;

    }

    /**
     * 获取数据对象
     * @param command
     * @return
     */
    private static Class<? extends Packet> getRequestType(byte command){
        return packetMap.get(command);
    }

    /**
     * 根据序列化算法获取映射的序列化器
     * @param serializerAlgrothm
     * @return
     */
    private static Serializer getSerializer(byte serializerAlgrothm){
        return serializerMap.get(serializerAlgrothm);
    }
}
