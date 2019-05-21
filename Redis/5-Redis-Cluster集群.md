# Redis集群

## Redis集群方案有哪些

有两种： **codis架构**、**官方的redis cluster**。

## codis集群架构

codis是豌豆荚基于go编写的redis。

## 官方Redis Cluster集群方案

![](pictures/2.jpg)

特点：

- Redis官网推出的，先行扩展可以达到1000个节点
- 没有中心架构
- 一致性哈希思想
- 客户端直连redis

