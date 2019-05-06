package org.byron4j.cookbook.rocketmq.transaction;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 事务消息生产者
 */
@Slf4j
public class MQTransactionProducerDemo {

    public static void main(String[] args) throws MQClientException, InterruptedException {
        // 1. 创建事务生产者
        TransactionMQProducer   producer = new TransactionMQProducer("repay_tx_pro_group");

        // 2. 注册
        producer.setNamesrvAddr("localhost:5432");


        ExecutorService executorService = new ThreadPoolExecutor(4, 10, 100L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000), (Runnable r) -> {
            Thread t = new Thread(r);
            t.setName("repay-tx-mq-thread");
            return t;
        });

        // 3. 设置本地事务线程池
        producer.setExecutorService(executorService);

        // 4. 添加事务监听器
        producer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                //本地事务处理逻辑
                log.info("本地事务执行．··");
                log.info("消息标签是 " + new String (message.getTags()));
                log.info("消息内容是" + new String (message.getBody( ) ) );
                String tag= message.getTags() ;
                if (tag.equals ("Transaction1" )) {
                    //消息的标签，如果是Transactionl ，则返回事务失败标记
                    log.error("模拟本地事务执行失败");
                    //表示本地事务执行失败，当事务执行失败时需要返回 ROLLBACK 消息
                    return LocalTransactionState.ROLLBACK_MESSAGE ;
                }

                log.info("模拟本地事务执行成功");
                //表示本地事务执行成功
                return LocalTransactionState.COMMIT_MESSAGE ;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                log.info("服务器调用消息回查接口...");
                log.info("消息标签:" + new String(messageExt.getTags()));
                log.info("消息内容:" + new String((messageExt.getBody())));
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });

        // 5. 启动生产者
        producer.start();

        // 模拟发送事务消息
        for (int i = 0 ; i < 2; i++){
            Message message = new Message("tx-mq-TOPIC", "Transaction" + i, ("事务消息" + i).getBytes());
            TransactionSendResult transactionSendResult = producer.sendMessageInTransaction(message, null);
            log.info("事务消息发送结果:" + transactionSendResult);
            TimeUnit.MICROSECONDS.sleep(100);
        }

        Thread.sleep(10000);
        producer.shutdown();

    }
}
