package org.byron4j.cookbook.rocketmq.transaction;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 事务消息消费者
 */
@Slf4j
public class MQTransactionConsumerDemo {

    public static void main(String[] args) throws MQClientException {
        // 1. 创建推送型消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("repay_tx_con_group");

        // 2. 注册
        consumer.setNamesrvAddr("localhost:9876");

        // 3. 订阅主题
        consumer.subscribe("tx-mq-TOPIC", "*");

        // 4. 从哪里开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        // 5. 推送型消费者一定需要注册监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            Random r = new Random();
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for (MessageExt msg : list){
                    log.info("接收到的消息是:" + msg);
                    log.info("接收到的消息标签是:" + new String(msg.getTags()));
                    log.info("接收到的消息内容是:" + new String(msg.getBody()));
                }

                try{
                    // 模拟业务处理耗时
                    TimeUnit.SECONDS.sleep(r.nextInt());
                }catch (Exception e){
                    log.error("业务处理异常:", e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        // 6. 别玩了启动消费者
        consumer.start();
    }
}
