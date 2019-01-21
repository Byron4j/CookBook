package org.byron4j.cookbook.netty.apidemo.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.byron4j.cookbook.netty.apidemo.command.Command;

/**
 * 登陆对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestPacket extends Packet {

    private Integer userId;

    private String username;

    private String password;

    @Override
    public Byte getCommand() {
        return Command.LOGIN_REQUEST;
    }
}
