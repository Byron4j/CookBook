package org.byron4j.cookbook.rocketmq;

import io.netty.util.CharsetUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.Date;
import java.util.List;

public class MQConsumerDemo {
    public static void main(String[] args) throws MQClientException {
        // 1. 创建消费者，并设置组
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("repay_order_handler_group");

        // 2. 指定 NameServer
        consumer.setNamesrvAddr("localhost:9876;localhost:2058");

        // 3. 设置第一次启动时是从队列头还是尾开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        // 4. 订阅 Topic 下的消息, * 表示订阅所有的
        consumer.subscribe("TopicTest", "*");

        // 5. 推送型 Push Consumer 要注册消费监昕器， 当监听器被触发后才开始消费消息
        /*consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                if( null != msgs ){
                    for( MessageExt ele : msgs ){
                        System.out.println(new Date() + "" + new String(ele.getBody(), CharsetUtil.UTF_8));
                    }
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });*/

        // 可以设置批量处理的数量，默认为1
        //consumer.setConsumeMessageBatchMaxSize(100);
        // lambda表达式
        consumer.registerMessageListener( (List<MessageExt> msgs, ConsumeOrderlyContext context) -> {
            if( null != msgs ){
                for( MessageExt ele : msgs ){
                    System.out.println(new Date() + "" + new String(ele.getBody(), CharsetUtil.UTF_8));
                }
            }
            return ConsumeOrderlyStatus.SUCCESS;
        } );

        // 6. 启动消费者
        consumer.start();
        System.out.println("消费者已经启动...");
    }
}
