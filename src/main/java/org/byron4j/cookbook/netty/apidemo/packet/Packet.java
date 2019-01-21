package org.byron4j.cookbook.netty.apidemo.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户端、服务端通信过程中的java对象
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Packet {
    /**
     * 协议版本号
     */
    private Byte version = 1;

    /**
     * 获取指令
     * @return
     */
    public abstract Byte getCommand();
}
