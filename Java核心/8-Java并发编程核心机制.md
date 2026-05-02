# Java并发编程核心机制

## 1. 概述

Java 并发编程涉及多线程协作、共享数据安全、性能优化等多个维度。掌握核心机制是编写高并发系统的基石。

```
并发编程知识体系:

基础机制
├── volatile      → 可见性、禁止指令重排序
├── synchronized  → 互斥锁、对象监视器
├── CAS           → 乐观锁、原子操作
└── AQS           → 队列同步器（已覆盖）

线程协作
├── wait/notify   → 线程等待/唤醒
├── join          → 线程合并
├── CountDownLatch → 倒计时门闩
├── CyclicBarrier  → 循环栅栏
└── Semaphore      → 信号量

线程局部
├── ThreadLocal   → 线程本地变量
└── InheritableThreadLocal → 父子线程传递

异步编程
├── Future/Callable → 异步任务
├── CompletableFuture → 组合式异步
└── ForkJoinPool    → 分治任务

并发集合
├── ConcurrentHashMap → 线程安全Map
├── CopyOnWriteArrayList → 写时复制List
├── BlockingQueue     → 阻塞队列
└── ConcurrentLinkedQueue → 无锁队列
```

## 2. volatile 关键字

### 2.1 内存可见性

```
问题：线程间变量不可见

线程A:               线程B:
read flag = false   read flag = false
                    flag = true（修改副本）
                    （未刷新到主内存）
read flag = false   
（永远看不到 true）

解决方案：volatile

主内存:
flag = true

线程A缓存:           线程B缓存:
flag = true  ←──────  写入后立即刷新
（立即可见）          到主内存

volatile 保证:
1. 写操作立即刷新到主内存
2. 读操作直接从主内存读取
3. 禁止指令重排序
```

### 2.2 适用场景

```java
// 状态标志
private volatile boolean running = true;

public void shutdown() {
    running = false;  // 所有线程立即可见
}

// 双重检查锁（DCL）
private volatile static Singleton instance;

public static Singleton getInstance() {
    if (instance == null) {           // 第一次检查（无锁）
        synchronized (Singleton.class) {
            if (instance == null) {   // 第二次检查（有锁）
                instance = new Singleton(); // volatile禁止重排序
            }
        }
    }
    return instance;
}
```

### 2.3 不保证原子性

```java
// 错误用法：volatile 不保证原子性
private volatile int count = 0;

public void increment() {
    count++;  // 非原子操作：读取→修改→写入
}

// 多线程并发时仍会出现丢失更新
// 解决方案：使用 AtomicInteger
private AtomicInteger count = new AtomicInteger(0);

public void increment() {
    count.incrementAndGet();  // CAS 保证原子性
}
```

## 3. synchronized 关键字

### 3.1 三种用法

```java
// 1. 同步实例方法（锁对象实例）
public synchronized void method() {
    // 临界区
}

// 2. 同步静态方法（锁类对象）
public static synchronized void staticMethod() {
    // 临界区
}

// 3. 同步代码块（灵活控制锁粒度）
public void method() {
    synchronized (this) {  // 锁当前对象
        // 临界区
    }
    
    synchronized (obj) {   // 锁指定对象
        // 临界区
    }
    
    synchronized (ClassName.class) {  // 锁类
        // 临界区
    }
}
```

### 3.2 锁升级过程

```
JDK 1.6 之前的 synchronized（重量级锁）:

线程请求锁 → 未获取到 → 进入等待队列 → 操作系统挂起 → 上下文切换
                        ↑______________________________|
                                    （性能开销大）

JDK 1.6 之后的锁升级（优化）:

无锁 → 偏向锁 → 轻量级锁 → 重量级锁

1. 偏向锁（Biased Locking）:
   - 只有一个线程访问时，记录线程 ID
   - 再次进入无需 CAS，只需检查线程 ID
   
2. 轻量级锁（Lightweight Locking）:
   - 多个线程交替访问（无竞争）
   - 使用 CAS 自旋尝试获取锁
   - 避免线程阻塞
   
3. 重量级锁（Heavyweight Locking）:
   - 多个线程同时竞争
   - 线程阻塞，操作系统调度

锁升级流程:

对象头 Mark Word:

无锁:    [hashcode | age | 0 | 01]
偏向锁:  [threadID | epoch | age | 1 | 01]
轻量级:  [指向栈中锁记录的指针 | 00]
重量级:  [指向互斥量（Monitor）的指针 | 10]
```

### 3.3 与 ReentrantLock 对比

| 特性 | synchronized | ReentrantLock |
|------|-------------|---------------|
| 实现方式 | JVM 内置（字节码指令） | API 层面（Java 代码） |
| 锁获取 | 自动获取/释放 | 手动 lock()/unlock() |
| 可重入 | 支持 | 支持 |
| 公平性 | 非公平 | 支持公平/非公平 |
| 中断响应 | 不支持 | 支持 lockInterruptibly() |
| 超时获取 | 不支持 | 支持 tryLock(timeout) |
| 条件变量 | 一个（wait/notify） | 多个（Condition） |
| 性能 | JDK6+ 优化后接近 | 略优（极端场景） |

## 4. CAS 乐观锁

### 4.1 原理

```
CAS（Compare And Swap）:

内存值 V、预期值 A、新值 B

如果 V == A，则将 V 更新为 B，返回 true
如果 V != A，说明被其他线程修改，返回 false

流程:

线程1:                    线程2:
读取 V = 10              读取 V = 10
                         CAS(V, 10, 20) → 成功，V = 20
CAS(V, 10, 30) → 失败
（V 已被改为 20）
重试: 读取 V = 20
CAS(V, 20, 30) → 成功，V = 30

ABA 问题:

线程1:                    线程2:
读取 V = A               读取 V = A
                         CAS(V, A, B) → 成功，V = B
                         CAS(V, B, A) → 成功，V = A
CAS(V, A, C) → 成功
（但 V 已经经历了 A→B→A 的变化）

解决方案：AtomicStampedReference（带版本号）
```

### 4.2 原子类

```java
// 基本类型
AtomicInteger atomicInt = new AtomicInteger(0);
atomicInt.incrementAndGet();     // ++i
atomicInt.getAndIncrement();     // i++
atomicInt.addAndGet(5);          // i += 5
atomicInt.compareAndSet(0, 1);   // CAS

// 引用类型
AtomicReference<String> atomicRef = new AtomicReference<>("A");
atomicRef.compareAndSet("A", "B");

// 带版本号（解决 ABA）
AtomicStampedReference<String> stampedRef = 
    new AtomicStampedReference<>("A", 0);
stampedRef.compareAndSet("A", "B", 0, 1);

// 字段更新器
AtomicIntegerFieldUpdater<User> updater = 
    AtomicIntegerFieldUpdater.newUpdater(User.class, "age");
updater.incrementAndGet(user);
```

## 5. ThreadLocal

### 5.1 原理

```
ThreadLocal 原理:

每个 Thread 对象内部维护一个 ThreadLocalMap:

Thread {
    ThreadLocal.ThreadLocalMap threadLocals;
}

ThreadLocalMap {
    Entry[] table;
    
    static class Entry extends WeakReference<ThreadLocal<?>> {
        Object value;
    }
}

数据存取:

ThreadLocal<String> local = new ThreadLocal<>();

set("value"):
Thread.currentThread().threadLocals
    .set(this, "value");
    // key = ThreadLocal 实例（弱引用）
    // value = "value"

get():
Thread.currentThread().threadLocals
    .getEntry(this).value;

内存泄漏风险:

ThreadLocal（弱引用）        Value（强引用）
    ↓                           ↑
    └──→ 被 GC 回收              │
         但 Value 无法被回收 ←───┘
         （因为 Thread → ThreadLocalMap → Entry → Value）
         
解决方案:
1. 使用完调用 remove()
2. 使用 static 修饰 ThreadLocal
3. 使用 try-finally 确保清理
```

### 5.2 使用场景

```java
// 数据库连接上下文
public class ConnectionContext {
    private static final ThreadLocal<Connection> 
        CONNECTION = new ThreadLocal<>();
    
    public static void set(Connection conn) {
        CONNECTION.set(conn);
    }
    
    public static Connection get() {
        return CONNECTION.get();
    }
    
    public static void remove() {
        CONNECTION.remove();  // 必须清理！
    }
}

// SimpleDateFormat 线程安全
private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT =
    ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

public static String format(Date date) {
    return DATE_FORMAT.get().format(date);
}
```

### 5.3 InheritableThreadLocal

```java
// 子线程继承父线程的 ThreadLocal 值
InheritableThreadLocal<String> inheritable = 
    new InheritableThreadLocal<>();

inheritable.set("父线程的值");

new Thread(() -> {
    System.out.println(inheritable.get());  // "父线程的值"
}).start();

// 线程池中的传递问题：
// 使用 TransmittableThreadLocal（阿里开源）
```

## 6. CompletableFuture 异步编程

### 6.1 创建异步任务

```java
// 1. runAsync：无返回值
CompletableFuture.runAsync(() -> {
    System.out.println("异步任务");
});

// 2. supplyAsync：有返回值
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return "结果";
});

// 3. 指定线程池
ExecutorService executor = Executors.newFixedThreadPool(4);
CompletableFuture<String> future = CompletableFuture.supplyAsync(
    () -> "结果", 
    executor
);
```

### 6.2 组合式异步

```java
// 串行组合
thenApply:    T -> U      （有入参有返回值）
thenAccept:   T -> void   （有入参无返回值）
thenRun:      () -> void  （无入参无返回值）

// 并行组合
thenCombine:     (T, U) -> V    （两任务并行，都完成后合并）
thenAcceptBoth:  (T, U) -> void （两任务并行，都完成后消费）

// 异常处理
exceptionally:   Throwable -> T     （异常恢复）
handle:          (T, Throwable) -> U （统一处理正常和异常）

示例:
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> fetchOrder(orderId))      // 查询订单
    .thenApply(order -> calculatePrice(order))   // 计算价格
    .thenApply(price -> applyDiscount(price))    // 应用折扣
    .exceptionally(ex -> {
        log.error("处理失败", ex);
        return "0.00";
    });

String result = future.join();  // 阻塞获取结果
```

### 6.3 多任务编排

```java
// allOf：所有任务完成
CompletableFuture<Void> all = CompletableFuture.allOf(
    task1, task2, task3
);
all.thenRun(() -> {
    // 所有任务完成后的逻辑
});

// anyOf：任一任务完成
CompletableFuture<Object> any = CompletableFuture.anyOf(
    task1, task2, task3
);
any.thenAccept(result -> {
    // 最快完成的任务结果
});
```

## 7. 并发集合

### 7.1 ConcurrentHashMap

```
JDK 1.7：分段锁（Segment）

[Segment 0] → [HashEntry[]]
[Segment 1] → [HashEntry[]]
[Segment 2] → [HashEntry[]]
...
[Segment 15] → [HashEntry[]]

每个 Segment 独立加锁，默认 16 个 Segment

JDK 1.8：CAS + synchronized

数组 + 链表 + 红黑树（与 HashMap 结构类似）

- 插入：CAS 尝试，失败则 synchronized 锁定头节点
- 查询：无锁，volatile 保证可见性
- 扩容：多线程协助迁移
```

### 7.2 CopyOnWriteArrayList

```
写时复制（Copy-On-Write）:

读操作：直接读取数组（无锁）
写操作：复制新数组，修改后替换引用

add() 流程:
1. 获取 ReentrantLock
2. 复制当前数组（Object[] newElements = Arrays.copyOf(elements, len + 1)）
3. 新数组末尾添加元素
4. 替换引用（elements = newElements）
5. 释放锁

适用场景：读多写少
缺点：内存占用大，数据实时性弱
```

### 7.3 BlockingQueue

```java
// ArrayBlockingQueue：有界队列，数组实现
BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);

// LinkedBlockingQueue：可选有界，链表实现
BlockingQueue<String> queue = new LinkedBlockingQueue<>();

// SynchronousQueue：不存储元素，直接传递
BlockingQueue<String> queue = new SynchronousQueue<>();

// DelayQueue：延迟队列，元素到期才能取出
BlockingQueue<Delayed> queue = new DelayQueue<>();

// 常用方法:
queue.put(item);      // 队列满时阻塞
queue.take();         // 队列空时阻塞
queue.offer(item, timeout, TimeUnit);  // 超时等待
queue.poll(timeout, TimeUnit);         // 超时等待
```

## 8. 最佳实践

1. **volatile 只用于状态标志**，不要用于计数器（无法保证原子性）
2. **优先使用 synchronized**，只有需要超时/中断/多条件时才用 ReentrantLock
3. **ThreadLocal 必须 remove()**，避免内存泄漏
4. **CompletableFuture 要处理异常**，避免异常吞没
5. **选择合适的并发集合**：
   - 读多写少 → CopyOnWriteArrayList
   - 高并发 Map → ConcurrentHashMap
   - 生产者消费者 → BlockingQueue
6. **避免在锁内调用外部方法**，防止死锁
7. **减少锁粒度**，锁只保护必要代码