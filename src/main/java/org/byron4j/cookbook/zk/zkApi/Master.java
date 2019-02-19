package org.byron4j.cookbook.zk.zkApi;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * 建立自己的监视点（Watcher）
 */

public class Master implements Watcher {

    ZooKeeper zk;

    String hostPort;

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

    public static void main(String[] args) throws Exception {
        Master master = new Master("localhost:2181,localhost:2182,localhost:2183");

        master.startZk();

        Thread.sleep(60000);
    }
}
