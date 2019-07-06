package org.byron4j.cookbook.netty.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.byron4j.cookbook.netty.apidemo.command.Command;
import org.byron4j.cookbook.netty.apidemo.packet.Packet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponsePacket extends Packet {

    /**消息内容*/
    private String message;

    /**
     * 消息请求命令
     * @return
     */
    @Override
    public Byte getCommand() {
        return Command.MESSAGE_RESPONSE;
    }
}
