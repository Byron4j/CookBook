package org.byron4j.cookbook.zk.chapter4;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 建立自己的监视点（Watcher）
 */

@Slf4j
public class Master7 implements Watcher {

    static ZooKeeper zk;

    String hostPort;

    static String serverId = Integer.toHexString(new Random().nextInt());


    @Override
    public void process(WatchedEvent event) {
        System.out.println("process:" + event);
    }

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
