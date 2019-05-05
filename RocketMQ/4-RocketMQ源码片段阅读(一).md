# RocketMQ部分源码阅读理解

## 生产者消息压缩

```
+-- org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl#sendKernelImpl
	+-- org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl#tryToCompressMessage
		+-- org.apache.rocketmq.common.UtilAll#compress
```

RocketMQ的该工具类使用了jdk的工具类进行了封装：

```java
public static byte[] compress(byte[] src, int level) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(src.length);
    Deflater defeater = new Deflater(level);
    DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, defeater);

    byte[] result;
    try {
        deflaterOutputStream.write(src);
        deflaterOutputStream.finish();
        deflaterOutputStream.close();
        result = byteArrayOutputStream.toByteArray();
    } catch (IOException var14) {
        defeater.end();
        throw var14;
    } finally {
        try {
            byteArrayOutputStream.close();
        } catch (IOException var13) {
            ;
        }

        defeater.end();
    }

    return result;
}
```