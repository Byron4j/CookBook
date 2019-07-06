# AQS框架详解

## RocketMQ中一个简单的AQS实现

org.apache.rocketmq.common.CountDownLatch2.Sync

```java
/**
 * Synchronization control For CountDownLatch2.
 * Uses AQS state to represent count.
 */
private static final class Sync extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = 4982264981922014374L;

    private final int startCount;

    Sync(int count) {
        this.startCount = count;
        setState(count);
    }

    int getCount() {
        return getState();
    }

    protected int tryAcquireShared(int acquires) {
        return (getState() == 0) ? 1 : -1;
    }

    protected boolean tryReleaseShared(int releases) {
        // Decrement count; signal when transition to zero
        for (; ; ) {
            int c = getState();
            if (c == 0)
                return false;
            int nextc = c - 1;
            if (compareAndSetState(c, nextc))
                return nextc == 0;
        }
    }

    protected void reset() {
        setState(startCount);
    }
}

```