package org.byron4j.cookbook.rocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

public class MQProducerDemo {
    public static void main(String[] args) throws MQClientException, InterruptedException {


        // 1. 创建一个生产者，并设置消息生产组
        DefaultMQProducer producer = new DefaultMQProducer("repay_order_create_group");

        // 2. 指定生产者要关联的 NameServer 地址
        producer.setNamesrvAddr("localhost:9876;localhost:2058");



        // 3. 启动生产者
        /*
        启动这个生成器实例。需要执行许多内部初始化过程来准备这个实例，因此，在发送或查询消息之前必须调用这个方法。
         */
        producer.start();


        // 4. 生产者发送消息
        for (int i = 0; i < 50; i++) {
            try {

                /*
                 * 创建一个消息实力，指定topic、标签和消息体
                 */
                Message msg = new Message("TopicTest" /* Topic主题 */,
                        "TagA" /* Tag 子主题*/,
                        ("Hello RocketMQ 消息" + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* 消息体 */
                );

                /*
                 * 生产者发送消息传递给Broker
                 */
                SendResult sendResult = producer.send(msg);

                System.out.printf("%s%n", sendResult);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }

        // 5. 关闭资源，注销自己
        producer.shutdown();
    }
}
