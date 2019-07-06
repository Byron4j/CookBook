package org.byron4j.cookbook.netty.apidemo.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.byron4j.cookbook.netty.apidemo.command.Command;

/**
 * 登陆响应对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponsePacket extends Packet {

    private boolean success;

    private String failCode;

    private String failReason;

    /**
     * 登陆响应指令
     * @return
     */
    @Override
    public Byte getCommand() {
        return Command.LOGIN_RESPONSE;
    }
}
