package org.byron4j.cookbook.netty.message;

import io.netty.util.AttributeKey;

public interface Attributes {

    /**登陆属性*/
    AttributeKey<Boolean> LOGIN = AttributeKey.newInstance("login");
}
