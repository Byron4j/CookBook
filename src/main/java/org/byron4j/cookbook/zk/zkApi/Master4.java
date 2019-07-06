package org.byron4j.cookbook.zk.zkApi;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Random;

/**
 * 建立自己的监视点（Watcher）
 */

@Slf4j
public class Master4 implements Watcher {

    static ZooKeeper zk;

    static String hostPort;

    static String serverId = Integer.toHexString(new Random().nextInt());

    static boolean isLeader = false;

    // 创建create回调方法对象
    static AsyncCallback.StringCallback masterCreateCallback = new AsyncCallback.StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    checkMaster();
                    return;
                case OK:
                    isLeader = true;
                    break;
                default:
                    isLeader = false;
            }

            System.out.println("I'm " + (isLeader ? "" : "not ") + "the leader");
        }
    };


    // 创建 getData 回调方法对象
    static AsyncCallback.DataCallback masterCheckCallback = new AsyncCallback.DataCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    checkMaster();
                    return;
                case NONODE:
                    runForMaster();
                    return;
            }
        }
    };

    // 创建主从模型目录的回调方法对象
    static AsyncCallback.StringCallback createParentCallback = new AsyncCallback.StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)){
                case OK:
                    log.info("Parent " + path + " created");
                    break;
                case CONNECTIONLOSS:
                    createParent(path, (byte[]) ctx);
                    break;
                case NODEEXISTS:
                    log.warn("Parent already registered: " + path);
                    break;
                default:
                    log.error("Something went wrong: ", KeeperException.create(KeeperException.Code.get(rc), path));
            }
        }
    };

    /**
     * 初始化时，创建主从模式的三个目录
     */
    static void bootstrap(){
        createParent("/workers", new byte[0]);
        createParent("/tasks", new byte[0]);
        createParent("/assign", new byte[0]);
    }

    Master4(String hostPort){
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

    static void runForMaster() {
        zk.create("/master", serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, masterCreateCallback, null);
    }

    /**
     * 如果存在了 /master 返回true
     * @return
     */
    static void checkMaster(){
        zk.getData("/master",false, masterCheckCallback, null);
    }

    /**
     * 创建 /workers、/tasks、/assign
     * @param path
     * @param data
     */
    static void createParent(String path, byte[] data){
        zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, createParentCallback, data);
    }

    public static void main(String[] args) throws Exception {
        Master4 master = new Master4("localhost:2181,localhost:2182,localhost:2183");

        master.startZk(); // 连接到Zk服务器

        // 尝试创建 /workers、/tasks、/assign
        bootstrap();

        master.runForMaster();

        if(master.isLeader){
            System.out.println("I'm a leader.");
        }else{
            System.out.println("Someone else is the leader.");
        }
        Thread.sleep(60000); // 主线程休眠60s，观察一下后台进程的运行情况
        master.stopZk(); // 关闭Zk会话
    }
}
