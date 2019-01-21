package org.byron4j.cookbook.netty.apidemo.serializer;

import com.alibaba.fastjson.JSON;

/**
 * 实现的JSON序列化器
 */
public class JSONSerializer implements  Serializer {
    /**
     * 序列化算法
     * @return
     */
    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.JSON;
    }

    /**
     * 序列化
     * @param object
     * @return
     */
    @Override
    public byte[] serialzer(Object object) {
        return JSON.toJSONBytes(object);
    }

    /**
     * 反序列化
     * @param clazz
     * @param bytes
     * @param <T>
     * @return
     */
    @Override
    public <T> T deserialzer(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
