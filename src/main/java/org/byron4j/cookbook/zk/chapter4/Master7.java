package org.byron4j.cookbook.zk.chapter4;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 建立自己的监视点（Watcher）
 */

@Slf4j
public class Master7 implements Watcher {

    static ZooKeeper zk;

    String hostPort;

    static String serverId = Integer.toHexString(new Random().nextInt());

    ExecutorService executorService = Executors.newFixedThreadPool(4);

    /**执行中的任务列表*/
    List<String> runingTasks = Collections.emptyList();

    @Override
    public void process(WatchedEvent event) {
        if( event.getType() == Event.EventType.NodeChildrenChanged ){
            // 子节点变化事件
            assert  new String("/assign/worker-" + serverId).equals(event.getPath());
            getTasks();
        }
    }

    void getTasks(){
        zk.getChildren("assign/worker-" + serverId,
                this,
                tasksGetChildrenCallback,
                null);
    }

    AsyncCallback.ChildrenCallback tasksGetChildrenCallback = new AsyncCallback.ChildrenCallback(){

        @Override
        public void processResult(int rc, String path, Object ctx, List<String> children) {
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    getTasks();
                    break;
                case OK:
                    if( children != null ){
                        // 存在子节点
                        executorService.execute(new Runnable() {
                            List<String> children;
                            DataCallback cb;
                            @Override
                            public void run() {
                                log.info("遍历任务");
                                synchronized (runingTasks){
                                    for(  String task: children){
                                        if( runingTasks.contains(task) ){
                                            log.trace("New task:{}", task);
                                            zk.getData("assign/worker-" + serverId + "/" + task,
                                                    false,
                                                    cb,
                                                    task);
                                        }
                                    }
                                }
                            }
                        });
                    }
            }
        }
    };

    Master7(String hostPort){
        this.hostPort = hostPort;
    }

    void startZk() throws IOException {
        zk = new ZooKeeper(this.hostPort, 15000, this);
    }

    /*********************************从节点注册到Zk START*************************************/
    /**
     * 通过创建一个 Znode 节点来注册从节点
     * 重试时，如果节点已经存在，则我们会收到一个 NODEEXISTS 事件
     */
    void register(){
        zk.create("/workers/worker-" + serverId,
                new byte[0],
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL,
                createWorkerCallback,
                null);
    }

    AsyncCallback.StringCallback createWorkerCallback = new AsyncCallback.StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)){
                case OK:
                    log.info("Registerd successfully:" + serverId);
                    break;
                case CONNECTIONLOSS:
                    register();
                    break;
                default:
                    log.error("Registered failed, something went wrong:" + KeeperException.create(KeeperException.Code.get(rc), path));
            }
        }
    };
    /*********************************从节点注册到Zk END*************************************/



}
