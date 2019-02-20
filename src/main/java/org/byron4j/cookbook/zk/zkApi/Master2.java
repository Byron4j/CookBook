package org.byron4j.cookbook.zk.zkApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Random;

/**
 * 建立自己的监视点（Watcher）
 */

public class Master2 implements Watcher {

    ZooKeeper zk;

    String hostPort;

    String serverId = Integer.toHexString(new Random().nextInt());

    boolean isLeader = false;

    Master2(String hostPort){
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

    void runForMaster() throws InterruptedException {
        while(true){
            try {
                zk.create("/master", serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                // 创建完成，为主节点
                isLeader = true;
                break;
            } catch (KeeperException.NodeExistsException e) {
                isLeader = false;
                break;
            }catch (KeeperException.ConnectionLossException ex){

            }catch (KeeperException ex2){

            }
            // 检查 /master 是否存在
            if(checkMaster()){
                break;
            }
        }
    }

    /**
     * 如果存在了 /master 返回true
     * @return
     */
    boolean checkMaster(){
        while(true){
            Stat stat = new Stat();
            try {
                byte[] data = zk.getData("/master", false, stat);

                isLeader = new String(data).equals(serverId);
                return true;
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Master2 master = new Master2("localhost:2181,localhost:2182,localhost:2183");

        master.startZk();

        master.runForMaster();

        if(master.isLeader){
            System.out.println("I'm a leader.");
        }else{
            System.out.println("Someone else is the leader.");
        }
        Thread.sleep(60000);
        master.stopZk();
    }
}
