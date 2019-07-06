package org.byron4j.cookbook.javacore.thpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorDemo {

    static ExecutorService es = new ThreadPoolExecutor(4, 200,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024),
            new ExecutorDemo.DefaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());
    static int cnt = 10000;

    public static void main(String[] args){
        while(cnt-- > 0){
            es.execute(() -> {System.out.println(Thread.currentThread().getName());
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        es.shutdown();
    }


    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "javacore-pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
