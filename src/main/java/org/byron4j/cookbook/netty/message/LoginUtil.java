package org.byron4j.cookbook.netty.message;


import io.netty.channel.Channel;

/**
 * 登陆校验
 */
public class LoginUtil {

    /**
     * 标记为已登录
     * @param channel
     */
    public static void markLogon(Channel channel){
        channel.attr(Attributes.LOGIN).set(true);
    }

    /**检查登陆标志*/
    public static boolean isLogon(Channel channel){
        return null != channel.attr(Attributes.LOGIN).get();
    }
}
