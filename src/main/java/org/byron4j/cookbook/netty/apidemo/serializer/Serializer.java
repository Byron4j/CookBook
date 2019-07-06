package org.byron4j.cookbook.netty.apidemo.serializer;

/**
 * 定义序列化器接口
 */
public interface Serializer {

    /**
     * 获取序列化方法
     * @return
     */
    byte getSerializerAlgorithm();

    /**
     * 将java对象序列化为byte数组
     * @param object
     * @return
     */
    byte[] serialzer(Object object);

    /**
     * 反序列化，将byte数组反序列化为指定的java累心对象
     * @param clazz
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserialzer(Class<T> clazz, byte[] bytes);


    /**
     * 默认的序列化器
     */
    Serializer DEFAULT = new JSONSerializer();
}
