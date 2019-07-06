package org.byron4j.cookbook.zk.zkFollow;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;

@Slf4j
public class Client implements Watcher {

    ZooKeeper zk;
    String hostPort;

    Client(String hostPort){
        this.hostPort = hostPort;
    }

    void startZk() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }

    String queueCommand(String command) throws Exception {
        while(true){
            try{
                String znodeName = zk.create("/tasks/task-", command.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
                return znodeName;
            }catch (KeeperException.NodeExistsException e){
                throw  new Exception("Already exists");
            }catch (KeeperException.ConnectionLossException ex){
                // 连接丢失，则不做任何处理，继续while循环重试
            }

        }
    }

    @Override
    public void process(WatchedEvent event) {

    }

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost:2181,localhost:2182,localhost:2183");
        client.startZk();// 连接到Zk

        String znodeName = client.queueCommand("repayNotify");
        log.info("Created " + znodeName);
    }
}
