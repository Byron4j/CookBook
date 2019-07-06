package org.byron4j.cookbook.zk.zkFollow;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Random;

@Slf4j
public class Worker implements Watcher {

    ZooKeeper zk;
    String hostPort;
    String serverId = Integer.toHexString(new Random().nextInt());
    String status;
    String name;

    @Override
    public void process(WatchedEvent event) {
        log.info("process:" + event + ": " + hostPort);
    }

    /**
     * 构造器
     * @param hostPort
     */
    Worker(String hostPort){
        this.hostPort = hostPort;
    }

    /**
     * 连接Zk服务器
     * @throws IOException
     */
    void startZk() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }

    /**
     * 关闭Zk会话
     * @throws InterruptedException
     */
    void stopZk() throws InterruptedException {
        zk.close();
    }

    // 创建 回调方法对象
    AsyncCallback.StringCallback createWorkCallback = new AsyncCallback.StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    register(); // 连接丢失，则重新连接重试
                    break;
                case OK:
                    name = path;
                    log.info("Registered successfully: " + serverId); // 输出创建OK的信息
                    break;
                case NODEEXISTS:
                    log.warn("Already registered: " + serverId); // 已经存在了
                    break;
                default:
                    log.error("Something went wrong:" + KeeperException.create(KeeperException.Code.get(rc), path));
            }
        }
    };

    /**
     * 注册从节点
     */
    void register(){
        zk.create("/workers/worker-" + serverId,
                "Idle".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL,
                createWorkCallback,
                null);
    }



    // 异步设置状态
    AsyncCallback.StatCallback statusUpdateCallback = new AsyncCallback.StatCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    updateStatus((String)ctx);
                    return;
            }
        }
    };

    /**
     * 设置节点状态
     * @param status
     */
    synchronized void updateStatus(String status){
        if( this.status == status ){
            zk.setData("/workers/worker-" + serverId, status.getBytes(), -1, statusUpdateCallback, status);
        }
    }

    /**
     * 设置节点状态
     * @param status
     */
    void setStatus(String status){
        this.status = status;
        updateStatus(status);
    }

    public static void main(String[] args) throws Exception {
        Worker worker = new Worker("localhost:2181,localhost:2182,localhost:2183");
        worker.startZk(); // 连接Zk

        worker.register(); // 注册从节点

        worker.setStatus("done");
        Thread.sleep(30000);

        worker.stopZk(); // 关闭会话
    }
}
