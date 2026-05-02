# 数据结构和算法章节总览

本章节按"概念 -> 来源 -> 知识点 -> 数据结构/算法 -> 最佳实践"递进组织，配套 `src/main/java/org/byron4j/cookbook/algrithms` 下的可运行代码与测试。

## 章节结构

### 数据结构

- [01-线性表](数据结构/01-线性表.md) - 数组实现、扩容策略、访问复杂度
- [02-链表](数据结构/02-链表.md) - 单链表、双链表、循环链表、LRU 缓存
- [03-栈、队列](数据结构/03-栈、队列.md) - LIFO/FIFO、双端队列、括号匹配
- [04-哈希](数据结构/04-哈希.md) - 哈希函数、冲突解决、负载因子
- [05-树](数据结构/05-树.md) - 二叉树、遍历方式、存储结构
- [06-二叉查找树](数据结构/06-二叉查找树.md) - BST 性质、插入删除、退化问题
- [07-红黑树](数据结构/07-红黑树.md) - 自平衡、旋转修复、TreeMap 实现
- [08-AVL树](数据结构/08-AVL树.md) - 严格平衡、四种旋转、平衡因子
- [09-B树](数据结构/09-B树.md) - 多路平衡、B+树、数据库索引
- [10-图](数据结构/10-图.md) - 邻接矩阵/表、DFS/BFS、最短路径

### 算法

#### 基础排序算法
- [1-快速排序](算法/1-快速排序.md) - 分治策略、分区过程、基准选择
- [2-归并排序](算法/2-归并排序.md) - 分治合并、稳定排序、链表排序
- [3-堆排序](算法/3-堆排序.md) - 大顶堆、堆化过程、原地排序
- [4-冒泡排序](算法/4-冒泡排序.md) - 交换排序、优化版本、教学用途
- [5-选择排序](算法/5-选择排序.md) - 选择类排序、交换次数少、堆排序对比
- [6-插入排序](算法/6-插入排序.md) - 插入类排序、二分插入、小数据优化

#### 搜索与图算法
- [7-二分查找](算法/7-二分查找.md) - 有序数组查找、边界问题、lower_bound/upper_bound
- [8-深度优先搜索](算法/8-深度优先搜索.md) - 递归/迭代实现、回溯、路径搜索
- [9-广度优先搜索](算法/9-广度优先搜索.md) - 队列实现、层级遍历、最短路径

#### 算法思想
- [10-动态规划](算法/10-动态规划.md) - 最优子结构、重叠子问题、背包问题
- [11-贪心算法](算法/11-贪心算法.md) - 贪心选择性质、活动选择、霍夫曼编码

#### 工程实践算法（Java 技术栈）
- [12-限流算法](算法/12-限流算法.md) - 计数器/滑动窗口/令牌桶/漏桶、Sentinel、分布式限流
- [13-布隆过滤器](算法/13-布隆过滤器.md) - 概率型数据结构、缓存穿透防护、误判率计算
- [14-一致性哈希](算法/14-一致性哈希.md) - 虚拟节点、数据倾斜、Redis Cluster、负载均衡
- [15-负载均衡算法](算法/15-负载均衡算法.md) - 轮询/随机/最少连接/源地址哈希、Ribbon、Nginx
- [16-分布式ID生成算法](算法/16-分布式ID生成算法.md) - Snowflake、Leaf、号段模式、时钟回拨
- [17-分布式锁算法](算法/17-分布式锁算法.md) - Redis/DB/ZK 实现、Redisson、锁续期、死锁处理
- [18-跳表](算法/18-跳表.md) - 概率性多层链表、Redis ZSet、并发跳表、范围查询

## 算法对比速查

### 排序算法对比

| 算法 | 平均时间 | 最坏时间 | 空间 | 稳定性 | 适用场景 |
|------|---------|---------|------|--------|---------|
| 快速排序 | O(nlogn) | O(n²) | O(logn) | 不稳定 | 通用，最快 |
| 归并排序 | O(nlogn) | O(nlogn) | O(n) | 稳定 | 链表排序 |
| 堆排序 | O(nlogn) | O(nlogn) | O(1) | 不稳定 | 内存受限 |
| 插入排序 | O(n²) | O(n²) | O(1) | 稳定 | 小数据/近乎有序 |
| 选择排序 | O(n²) | O(n²) | O(1) | 不稳定 | 理论教学 |
| 冒泡排序 | O(n²) | O(n²) | O(1) | 稳定 | 教学用 |

### 搜索算法对比

| 算法 | 时间复杂度 | 空间复杂度 | 适用场景 |
|------|-----------|-----------|---------|
| 二分查找 | O(log n) | O(1) | 有序数组 |
| DFS | O(V+E) | O(V) | 路径搜索、连通性 |
| BFS | O(V+E) | O(V) | 最短路径、层级遍历 |

### 工程实践算法对比

| 算法 | 核心用途 | 时间复杂度 | 空间复杂度 | 生产框架 |
|------|---------|-----------|-----------|---------|
| 限流算法 | 流量控制 | O(1) | O(1) | Sentinel、Guava |
| 布隆过滤器 | 快速判断存在性 | O(k) | O(m) | Guava、RedisBloom |
| 一致性哈希 | 分布式分片 | O(log N) | O(N) | Redis Cluster、Nginx |
| 负载均衡 | 请求分发 | O(1) | O(N) | Nginx、Ribbon |
| 分布式ID | 全局唯一标识 | O(1) | O(1) | Leaf、Snowflake |
| 分布式锁 | 互斥访问 | O(1) | O(1) | Redisson、Curator |
| 跳表 | 有序集合 | O(log n) | O(n) | Redis、LevelDB |

## 代码索引

### 基础数据结构
- 线性表：`org.byron4j.cookbook.algrithms.list.LinearListDemo`
- 链表：`org.byron4j.cookbook.algrithms.list.SinglyLinkedListDemo`
- 栈队列：`org.byron4j.cookbook.algrithms.stackqueue.StackQueueDemo`
- 哈希表：`org.byron4j.cookbook.algrithms.hash.SimpleHashTableDemo`
- 树遍历：`org.byron4j.cookbook.algrithms.tree.BinaryTree`
- 二叉查找树：`org.byron4j.cookbook.algrithms.tree.BinarySearchTreeDemo`
- 红黑树（TreeMap）：`org.byron4j.cookbook.algrithms.tree.TreeMapRedBlackTreeDemo`
- AVL树：`org.byron4j.cookbook.algrithms.tree.AvlTreeDemo`
- B树查询：`org.byron4j.cookbook.algrithms.tree.BTreeSearchDemo`

### 基础算法
- 快速排序：`org.byron4j.cookbook.algrithms.QuickSort`

### 测试
- 测试用例：`org.byron4j.cookbook.algrithms.AlgorithmBasicsTest`
- 补全回归测试：`org.byron4j.cookbook.algrithms.DataStructureCompletenessTest`

## 运行方式

```bash
./mvnw -Dtest=AlgorithmBasicsTest,DataStructureCompletenessTest test
```

运行后可验证：

- 快速排序结果有序
- 线性表增删改查行为正确
- 二叉树前序/中序/后序遍历结果正确
- 链表、哈希、BST、AVL、B树查询示例行为正确

## 学习路径建议

### 初学者路径
1. **数据结构基础**: 线性表 → 链表 → 栈、队列 → 树
2. **基础算法**: 冒泡排序 → 插入排序 → 选择排序 → 二分查找
3. **简单应用**: 括号匹配、链表反转、二叉树遍历

### 进阶路径
1. **高级数据结构**: 二叉查找树 → AVL树 → 红黑树 → B树
2. **高级排序**: 快速排序 → 归并排序 → 堆排序
3. **图算法**: DFS → BFS → 最短路径

### 算法思想
1. **递归与分治**: 归并排序、快速排序
2. **动态规划**: 爬楼梯、背包问题、最长公共子序列
3. **贪心算法**: 活动选择、霍夫曼编码

### 工程实践路径（Java 技术栈）
1. **高并发基础**: 限流算法 → 布隆过滤器 → 跳表
2. **分布式基础**: 分布式ID → 一致性哈希 → 负载均衡
3. **分布式协调**: 分布式锁（Redis/ZK）
4. **框架集成**: Sentinel、Redisson、Ribbon、Nginx

### 面试重点
- **必会**: 快速排序、归并排序、二分查找、DFS、BFS
- **重点**: 动态规划（背包、最长递增子序列）
- **加分**: 一致性哈希、分布式锁、限流算法、布隆过滤器

## 参考资料

### 经典书籍
- 《算法导论》（Introduction to Algorithms）
- 《数据密集型应用系统设计》（Designing Data-Intensive Applications）
- 《Redis 设计与实现》
- 《深入理解 Java 虚拟机》

### 开源项目
- [LeetCode](https://leetcode.cn/) - 算法练习
- [VisuAlgo](https://visualgo.net/) - 算法可视化
- [Algorithm Visualizer](https://algorithm-visualizer.org/) - 算法可视化

### 框架文档
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Sentinel](https://sentinelguard.io/)
- [Redisson](https://redisson.org/)
- [Redis 官方文档](https://redis.io/documentation)