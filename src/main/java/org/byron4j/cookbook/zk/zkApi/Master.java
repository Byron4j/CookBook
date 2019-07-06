package org.byron4j.cookbook.zk.zkApi;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Random;

/**
 * 建立自己的监视点（Watcher）
 */

public class Master implements Watcher {

    ZooKeeper zk;

    String hostPort;

    String serverId = Integer.toHexString(new Random().nextInt());

    Master(String hostPort){
        this.hostPort = hostPort;
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("process: " + event);
    }

    void startZk() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }

    void stopZk() throws InterruptedException {
        zk.close();
    }

    void runForMaster() throws KeeperException, InterruptedException {
        /*
            path， 节点数据， 访问控制策略， 节点模式
         */
        zk.create("/master", serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

    }
    public static void main(String[] args) throws Exception {
        Master master = new Master("localhost:2181,localhost:2182,localhost:2183");

        master.startZk();

        master.runForMaster();

        Thread.sleep(60000);

        master.stopZk();
    }
}
