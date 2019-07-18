# MySQL存储引擎

存储引擎是MySQL的组件，用于处理不同类型表的SQL操作。
InnoDB 是默认的存储引擎，也是oracle公司首推的通用存储引擎，除非你需要处理特定的案例的时候则可以选择其他存储引擎。
```create table``` 语句默认使用InnoDB存储引擎。关于InnoDB存储引擎的更多细节介绍可以参考：https://dev.mysql.com/doc/refman/8.0/en/innodb-storage-engine.html


MySQL使用可插拔性的存储引擎架构，以便可以在运行中的MySQL服务中装载或者卸载指定的存储引擎。

```SHOW ENGINES ``` 语句可以查看你当前的MySQL服务支持哪些存储引擎。

## MySQL 8.0 支持的存储引擎

- InnoDB: 默认的存储引擎；InnoDB是一个事务安全的存储引擎，支持事务，拥有commit、rollback、崩溃恢复等机制保障用户数据。```InnoDB```行级锁和oracle风格的行读取无锁提升了读写性能。```InnoDB```将用户数据存储在聚集索引中，来减少基于主键的通用查询I/O开销。为了维护数据集成，```InnoDB```也支持```FOREIGN KEY```外键引用。

- MyISAM：这类表占空间较少。```表级锁``` 限制了读写性能，所以常用来建设只读、或者大部分情况下都是读居多的表。

- Memory：将所有的数据存储在RAM，主要用于在需要快速访问得到快速响应的环境中。该引擎以前也被称作```HEAP```引擎。但是这个引擎的使用场景已经越来越少了。```InnoDB```和它的缓冲池内存



- CSV： 该引擎的表是真正的文本文件，数据以逗号,分隔开。CSV引擎的表可以让你以CSV格式进行数据的导入、转储。因为CSV文件没有索引，所以通常情况在做一些正常操作时是以InnoDB存储的，只是在导入、导出时使用CSV。

- 其他的存储引擎，略。

## 各存储引擎的特性

你不需要受限于政客数据库都是用同一种数据库，可以针对单独的表使用不同的存储引擎。

例如，大都数情况下表都是InnoDB存储引擎的，有一个表使用的时CSV存储引擎，用于导出数据使用，一些表使用的是MEMORY存储引擎用于临时工作空间。

下表列出了各个存储引擎的相关特性，根据这些特性，可以根据自身的业务表选择合适的存储引擎。

<div class="table-contents">
<table frame="box" rules="all" summary="Summary of features supported per storage engine."><colgroup><col width="10%"><col width="16%"><col width="16%"><col width="16%"><col width="16%"><col width="16%"></colgroup><thead><tr><th scope="col">Feature</th>
<th scope="col">MyISAM</th>
<th scope="col">Memory</th>
<th scope="col">InnoDB</th>
<th scope="col">Archive</th>
<th scope="col">NDB</th>
</tr></thead><tbody><tr><td scope="row"><span class="bold"><strong>B-tree indexes</strong></span></td>
<td>Yes</td>
<td>Yes</td>
<td>Yes</td>
<td>No</td>
<td>No</td>
</tr><tr><td scope="row"><span class="bold"><strong>Backup/point-in-time recovery</strong></span> (note 1)</td>
<td>Yes</td>
<td>Yes</td>
<td>Yes</td>
<td>Yes</td>
<td>Yes</td>
</tr><tr><td scope="row"><span class="bold"><strong>Cluster database support</strong></span></td>
<td>No</td>
<td>No</td>
<td>No</td>
<td>No</td>
<td>Yes</td>
</tr><tr><td scope="row"><span class="bold"><strong>Clustered indexes</strong></span></td>
<td>No</td>
<td>No</td>
<td>Yes</td>
<td>No</td>
<td>No</td>
</tr><tr><td scope="row"><span class="bold"><strong>Compressed data</strong></span></td>
<td>Yes (note 2)</td>
<td>No</td>
<td>Yes</td>
<td>Yes</td>
<td>No</td>
</tr><tr><td scope="row"><span class="bold"><strong>Data caches</strong></span></td>
<td>No</td>
<td>N/A</td>
<td>Yes</td>
<td>No</td>
<td>Yes</td>
</tr><tr><td scope="row"><span class="bold"><strong>Encrypted data</strong></span></td>
<td>Yes (note 3)</td>
<td>Yes (note 3)</td>
<td>Yes (note 4)</td>
<td>Yes (note 3)</td>
<td>Yes (note 3)</td>
</tr><tr><td scope="row"><span class="bold"><strong>Foreign key support</strong></span></td>
<td>No</td>
<td>No</td>
<td>Yes</td>
<td>No</td>
<td>Yes (note 5)</td>
</tr><tr><td scope="row"><span class="bold"><strong>Full-text search indexes</strong></span></td>
<td>Yes</td>
<td>No</td>
<td>Yes (note 6)</td>
<td>No</td>
<td>No</td>
</tr><tr><td scope="row"><span class="bold"><strong>Geospatial data type support</strong></span></td>
<td>Yes</td>
<td>No</td>
<td>Yes</td>
<td>Yes</td>
<td>Yes</td>
</tr><tr><td scope="row"><span class="bold"><strong>Geospatial indexing support</strong></span></td>
<td>Yes</td>
<td>No</td>
<td>Yes (note 7)</td>
<td>No</td>
<td>No</td>
</tr><tr><td scope="row"><span class="bold"><strong>Hash indexes</strong></span></td>
<td>No</td>
<td>Yes</td>
<td>No (note 8)</td>
<td>No</td>
<td>Yes</td>
</tr><tr><td scope="row"><span class="bold"><strong>Index caches</strong></span></td>
<td>Yes</td>
<td>N/A</td>
<td>Yes</td>
<td>No</td>
<td>Yes</td>
</tr><tr><td scope="row"><span class="bold"><strong>Locking granularity</strong></span></td>
<td>Table</td>
<td>Table</td>
<td>Row</td>
<td>Row</td>
<td>Row</td>
</tr><tr><td scope="row"><span class="bold"><strong>MVCC</strong></span></td>
<td>No</td>
<td>No</td>
<td>Yes</td>
<td>No</td>
<td>No</td>
</tr><tr><td scope="row"><span class="bold"><strong>Replication support</strong></span> (note 1)</td>
<td>Yes</td>
<td>Limited (note 9)</td>
<td>Yes</td>
<td>Yes</td>
<td>Yes</td>
</tr><tr><td scope="row"><span class="bold"><strong>Storage limits</strong></span></td>
<td>256TB</td>
<td>RAM</td>
<td>64TB</td>
<td>None</td>
<td>384EB</td>
</tr><tr><td scope="row"><span class="bold"><strong>T-tree indexes</strong></span></td>
<td>No</td>
<td>No</td>
<td>No</td>
<td>No</td>
<td>Yes</td>
</tr><tr><td scope="row"><span class="bold"><strong>Transactions</strong></span></td>
<td>No</td>
<td>No</td>
<td>Yes</td>
<td>No</td>
<td>Yes</td>
</tr><tr><td scope="row"><span class="bold"><strong>Update statistics for data dictionary</strong></span></td>
<td>Yes</td>
<td>Yes</td>
<td>Yes</td>
<td>Yes</td>
<td>Yes</td>
</tr></tbody></table>
</div>



## 设置存储引擎

在创建一张新表时，你可以通过添加```ENGINE```操作在```CREATE TABLE```语句中来指定表的存储引擎。

```mysql
## 设置InnoDB存储引擎
create table t1(i int ) engine = INNODB;
CREATE TABLE t2 (i INT) ENGINE = CSV;
CREATE TABLE t3 (i INT) ENGINE = MEMORY;
```

如果你忽略了ENGINE操作，就会使用默认的存储引擎。MySQL8.0中默认使用的是InnoDB。你也可以通过在配置文件my.ini或者my.conf中指定```default-storage-engine=INNODB```来修改默认的存储引擎。


你也可以通过设置```default_storage_engine```变量来指定当前会话的默认存储引擎：
```mysql
set default_storage_engine = NDBCLUSTER;
```

修改表的存储引擎，可以使用```ALTER TABLE```语句：
```mysql
ALTER TABLE t engine = INNODB;
```






- [存储引擎特点介绍](https://jochen-z.com/articles/3/mysql-storage-engine)
空间已经提供了一个通用、稳定的方式以保证大都数、或者全部数据在内存中了，而且```NDBCLUSTER```为分布式数据集提供了非常快的 key-value 查找功能。
