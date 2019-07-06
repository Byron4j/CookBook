package org.byron4j.cookbook.netty.apidemo.command;

/**
 * 定义了一些关于指令的常量
 */
public interface Command {

    /**
     * 代表登陆请求的指令
     */
    Byte LOGIN_REQUEST = 1;

    /**
     * 登陆响应指令
     */
    Byte LOGIN_RESPONSE = 2;

    /**
     * 消息请求指令
     */
    Byte MESSAGE_REQUEST = 3;

    /**
     * 消息响应指令
     */
    Byte MESSAGE_RESPONSE = 4;
}
