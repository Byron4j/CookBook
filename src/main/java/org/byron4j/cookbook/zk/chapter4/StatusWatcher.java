package org.byron4j.cookbook.zk.chapter4;

import javafx.scene.input.KeyCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

#@Slf4j
public class StatusWatcher implements Watcher {

    ZooKeeper zk;
    String hostPort;

    StatusWatcher(String hostPort){
        this.hostPort = hostPort;
    }

    void startZk() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }

    @Override
    public void process(WatchedEvent event) {
        log.info("process: " + event);
    }

    public static void main(String[] args) throws IOException {
        StatusWatcher statusWatcher = new StatusWatcher("localhost:2181,localhost:2182,localhost:2183");
        statusWatcher.startZk();

        statusWatcher.zk.exists("/master", statusWatcher, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, Stat stat) {

            }
        }, null);
    }
}
