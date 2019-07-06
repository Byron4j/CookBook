package org.byron4j.cookbook.zk.zkFollow;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Date;

public class AdminClient implements Watcher {

    ZooKeeper zk;
    String hostPort;

    AdminClient(String hostPort){
        this.hostPort = hostPort;
    }

    void startZk() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }


    void listState() throws KeeperException, InterruptedException {
        try {
            Stat stat = new Stat();
            byte[] masterData = zk.getData("/master", false, stat);

            Date startDate = new Date(stat.getCtime());

            System.out.println("Master: " + new String(masterData) + " since " + startDate);
        }catch (KeeperException.NoNodeException e){
            System.out.println("No znode which named Master");
        }

        System.out.println("Workers:");

        for (String w: zk.getChildren("/workers", false)) {
            byte[] data = zk.getData("/workers/" + w, false, null);
            String state = new String(data);
            System.out.println("\t" + w + ": " + state);
        }

        System.out.println("Tasks:");
        for( String t : zk.getChildren("/tasks", null)){
            System.out.println("\t" + t);
        }

    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        AdminClient adminClient = new AdminClient("localhost:2181,localhost:2182,localhost:2183");
        adminClient.startZk();

        adminClient.listState();
    }
}
