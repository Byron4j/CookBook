# MySQL数据库读写锁示例详解、事务隔离级别示例详解

## 锁
- 性能分：乐观（比如使用version字段比对，无需等待）、悲观（需要等待其他事务）
>**乐观锁**，如它的名字那样，总是认为别人不会去修改，只有在提交更新的时候去检查数据的状态。通常是给数据增加一个字段来标识数据的版本。

>**悲观锁**，正如它的名字那样，数据库总是认为别人会去修改它所要操作的数据，因此在数据库处理过程中将数据加锁。其实现依靠数据库底层。
- 读锁(共享锁)、写锁(排他锁) 均属于悲观锁
- 粒度分：行级锁、表级锁

## 锁示例
### 对表加读锁：lock table tableName read 
所有session可以读；但是当前session 更新插入报错，其他session 更新插入等待。
比如： 在数据迁移时，加读锁，防止任何session的更新操作。


### 对表加写锁：lock table tableName write
当前session可以查询、更新；其他session阻塞读、写。

#### 读锁示范

##### 创建user表

```sql
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(20) DEFAULT '' COMMENT '姓名',
  `age` int(11) DEFAULT '0' COMMENT '年龄',
  `salary` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '薪水',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';
```

##### 插入数据

```sql
insert into `user` (`id`, `name`, `age`, `salary`, `version`) values('1','风清扬','60','2000.00','0');
insert into `user` (`id`, `name`, `age`, `salary`, `version`) values('2','朱元璋','80','3000.00','0');
insert into `user` (`id`, `name`, `age`, `salary`, `version`) values('3','孙猴子','50','4000.00','0');
```

##### 打开数据库session1、session2

在2个会话中查询到刚才插入的数据。

```shell
mysql> select * from user;
+----+--------+------+---------+---------+
| id | name   | age  | salary  | version |
+----+--------+------+---------+---------+
|  1 | 风清扬 |   60 | 2000.00 |       0 |
|  2 | 朱元璋 |   80 | 3000.00 |       0 |
|  3 | 孙猴子 |  500 | 4000.00 |       0 |
+----+--------+------+---------+---------+
3 rows in set (0.00 sec)
```


###### 第一步：在session1中对表user进行加读锁：

session1：

```shell
mysql> lock table user read;
Query OK, 0 rows affected (0.00 sec)
```

###### 第二步：session2进行读、写操作：

```shell
mysql> select * from user;
+----+--------+------+---------+---------+
| id | name   | age  | salary  | version |
+----+--------+------+---------+---------+
|  1 | 风清扬 |   60 | 2000.00 |       0 |
|  2 | 朱元璋 |   80 | 3000.00 |       0 |
|  3 | 孙猴子 |  500 | 4000.00 |       0 |
+----+--------+------+---------+---------+
3 rows in set (0.00 sec)

mysql> insert into `user` ( `name`, `age`, `salary`, `version`) values('唐玄奘','40','8000.00','0');


```
发现session2的插入操作会一直等待，等待session1释放锁。


###### 第三步：session中进行读没问题，在session1中进行更新操作会直接报错，提示<font color=red>表存在读锁不能被更新</font>：

```shell
mysql> insert into `user` ( `name`, `age`, `salary`, `version`) values('唐玄奘','40','8000.00','0');
ERROR 1099 (HY000): Table 'user' was locked with a READ lock and can't be updated
```


###### 第四步： 释放session1中的读锁

```shell
mysql> unlock  tables;
Query OK, 0 rows affected (0.00 sec)
```

此时会发现session2中等待的插入操作立马执行完毕：

```shell
mysql> insert into `user` ( `name`, `age`, `salary`, `version`) values('唐玄奘','40','8000.00','0');
Query OK, 1 row affected (6 min 38.15 sec)

mysql>

```

###### 第五步：session1给表1加读锁，查询表2时报错

```sql
## 对表user加读锁
mysql> lock table user read;
Query OK, 0 rows affected (0.00 sec)

## 可以对表user进行查询
mysql> select * from user limit 1;
+----+--------+------+---------+---------+
| id | name   | age  | salary  | version |
+----+--------+------+---------+---------+
|  1 | 风清扬 |   60 | 2000.00 |       0 |
+----+--------+------+---------+---------+
1 row in set (0.00 sec)


## 查询表employee时报错
mysql> select * from employee limit 1;
ERROR 1100 (HY000): Table 'employee' was not locked with LOCK TABLES
```

**读锁总结**： <u>对表加读锁，不影响任何session的读操作，都是共享的。 但是当前session执行更新操作会失败报错，其他session更新则会阻塞等待。因为会阻塞等待，所以读锁是悲观锁。对表加读锁后，session仅仅能对当前表进行操作，不能对其表操作</u>

**读锁的特点**：
- 持有表A读锁的session可以读表A，但是不能更新表A，也不能对其他表进行任何查询、更新操作
- 其他session可以读取表A，但是不能更新表A


#### 写锁示范

###### 第一步：session1 对表user加写锁

```sql
mysql> lock table user write;
Query OK, 0 rows affected (0.00 sec)
```

###### 第二部：session1 查询、更新操作不影响

```sql
mysql> select * from user;
+----+--------+------+---------+---------+
| id | name   | age  | salary  | version |
+----+--------+------+---------+---------+
|  1 | 风清扬 |   60 | 2000.00 |       0 |
|  2 | 朱元璋 |   80 | 3000.00 |       0 |
|  3 | 孙猴子 |  500 | 4000.00 |       0 |
|  4 | 唐玄奘 |   40 | 8000.00 |       0 |
+----+--------+------+---------+---------+
4 rows in set (0.00 sec)

mysql> insert into `user` ( `name`, `age`, `salary`, `version`) values('赵孟頫','20','1000.00','0');
Query OK, 1 row affected (0.04 sec)
```

###### 第三步： session2 查询、更新操作都会阻塞等待

sesion1查询：

```sql
mysql> select * from user;

```

session2更新：

```sql
mysql> insert into `user` ( `name`, `age`, `salary`, `version`) values('王羲之','40','3000.00','0');


```

###### 第四步： 释放session1的写锁，session2的更新操作立马完成

session1的写锁释放：

```sql
mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)
```


session2更新完成：

```sql
mysql> insert into `user` ( `name`, `age`, `salary`, `version`) values('王羲之','40','3000.00','0');
Query OK, 1 row affected (3 min 3.12 sec)

```


###### 第五步：session1给表1加写锁，查询表2时报错

```sql
## 对表user加写锁
mysql> lock table user read;
Query OK, 0 rows affected (0.00 sec)

## 可以对表user进行查询
mysql> select * from user limit 1;
+----+--------+------+---------+---------+
| id | name   | age  | salary  | version |
+----+--------+------+---------+---------+
|  1 | 风清扬 |   60 | 2000.00 |       0 |
+----+--------+------+---------+---------+
1 row in set (0.00 sec)


## 查询表employee时报错
mysql> select * from employee limit 1;
ERROR 1100 (HY000): Table 'employee' was not locked with LOCK TABLES
```


**写锁总结**： <u>对表加写锁，不影响当前session的操作，但是会影响其他session的读写操作，其他session任何操作都会阻塞等待。因为会阻塞等待，所以写锁是悲观锁。对表加写锁后，session仅仅能对当前表进行操作，不能对其表操作</u>

**写锁的特点**：

- session持有表A的写锁，则session可以对表A进行查询、更新操作
- 排他性，其他session不能对表A进行查询、更新操作

#### 读写锁注意事项

###### 对表A加锁后，当前session仅仅能操作表A

```sql
mysql> LOCK TABLES A READ;
mysql> SELECT COUNT(*) FROM A;
+----------+
| COUNT(*) |
+----------+
|        3 |
+----------+
mysql> SELECT COUNT(*) FROM B;
ERROR 1100 (HY000): Table 'B' was not locked with LOCK TABLES
```

###### 如果加锁时取了别名，则操作时只能使用别名

```sql
mysql> LOCK TABLE t AS myalias READ;
mysql> SELECT * FROM t;
ERROR 1100: Table 't' was not locked with LOCK TABLES
mysql> SELECT * FROM t AS myalias;
```

###### 不能在一次查询中多次引用加锁的表

```sql
mysql> LOCK TABLE t WRITE, t AS t1 READ;
mysql> INSERT INTO t SELECT * FROM t;
ERROR 1100: Table 't' was not locked with LOCK TABLES
mysql> INSERT INTO t SELECT * FROM t AS t1;
```



## 并发带来的问题

#### 更新丢失


#### [脏读](https://dev.mysql.com/doc/refman/8.0/en/glossary.html#glos_dirty_read)
事务A读取到了事务B修改的数据，但是事务B还未提交，事务B失败回滚，则事务A读取的无效，不符合一致性

#### [幻读](https://dev.mysql.com/doc/refman/8.0/en/innodb-next-key-locking.html)
事务A多次读取，后面读取读到了其他事务的提交新增的数据，导致查询结果前后不一，比如开始查询结果是1条，后面查询是2条。不符合隔离性

#### 不可重复读

事务A多次读取，后面读取读到了其他事务的提交，导致查询的数据已经变化。
比如开始读取id为1的姓名是xiaoming，后面再次读取发现姓名是lilei。不符合隔离性。



## 事务

A：原子性，全部执行全部不执行
C：一致性，开始和完成时数据状态一致
I：隔离性，事务之间隔离
D：持久性，事务完成是永久的

### 指定事务特征的语法


```sql

## 官方语法， 可以设置事务隔离级别与访问模式； 用于InnoDB表。
SET [GLOBAL | SESSION] TRANSACTION
    transaction_characteristic [, transaction_characteristic] ...

transaction_characteristic: {
    ISOLATION LEVEL level
  | READ WRITE   # 读写模式
  | READ ONLY	 # 只读模式
}

level: {
     REPEATABLE READ
   | READ COMMITTED
   | READ UNCOMMITTED
   | SERIALIZABLE
}

## 示例--将当前session的事务隔离级别设置为可重复读
 SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ ;

等效于：

mysql> set tx_isolation = 'repeatable-read';
Query OK, 0 rows affected (0.00 sec)

## 设置访问模式
SET TRANSACTION READ WRITE;
```

可选项 GLOBAL or SESSION 是用来指定执行的语句的范围为全局或者当前session。

MySQL 设置事务隔离级别从低到高分别为：
- 读未提交 : READ UNCOMMITTED
- 读已提交 : READ COMMITTED
- 可重复读 : REPEATABLE READ 默认级别
- 串行化   : SERIALIZABLE



**注意事项**：不允许在事务中指定 没有标明SESSION 或者 GLOBAL 的事务设置：

```sql
# 开启一个事务
mysql> START TRANSACTION;
Query OK, 0 rows affected (0.02 sec)

# 在开启的事务中设置事务特征是不允许的
mysql> SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
ERROR 1568 (25001): Transaction characteristics can't be changed
while a transaction is in progress
```


命令行设置隔离级别，可以参考：https://dev.mysql.com/doc/refman/8.0/en/server-system-variables.html#sysvar_transaction_isolation

```sql
## 命令行设置事务隔离级别
- set tx_isolation = 'REPEATABLE-READ' | 'READ-UNCOMMITTED' | 'READ-COMMITTED' |'SERIALIZABLE'
```



命令行演示，使用begin、commit演示多个session之间的事务





### 事务隔离级别示例

Mysql默认是可重复读。

- Mysql查看隔离级别命令
```shell
## 查看隔离级别
show variables like 'tx_isolation'

mysql> show variables like 'tx_isolation';
+---------------+-----------------+
| Variable_name | Value           |
+---------------+-----------------+
| tx_isolation  | REPEATABLE-READ |
+---------------+-----------------+
1 row in set, 1 warning (0.00 sec)
```


#### 读未提交 READ UNCOMMITTED

```shell
SET TRANSACTION ISOLATION LEVEL READ UMCOMMITED;
```

事务A可以读取事务B未提交的事务，会出现脏读。





#### 读已提交 READ COMMITTED

读提交都是读取自身session的最新快照。当通过锁读时（SELECT with FOR UPDATE or FOR SHARE），InnoDB 只会锁索引行记录，并不是他们之间的范围记录，如此便会允许其他事务插入新的记录。 间隙锁gap lock只会用在外键约束去检查重复键。

因为间隙锁不可用，所以可能出现幻读，其他session可以在间隙中插入数据。

只有基于行级的二进制日志支持 READ COMMITED 的隔离级别。如果使用 binlog_format = MIXED, 服务器自动使用基于行级的日志。


使用读提交的隔离级别，会有额外影响：
>- 对于UPDATE、DELETE 语句，InnoDB 仅仅持有更新或者删除的行的锁。 MySQL 在评估完 WHERE 条件之后，会释放不匹配的行的锁，大大减少了思索的可能性，但是还是有可能会发生死锁的。
- 对于 UPDATE 语句，如果一条记录已经被锁了，InnoDB会采取“半一致性” 的读方式，会返回最新的已经提交的版本记录给MySQL，来匹配WHERE条件是否匹配。 如果存在记录匹配到了（必须会被UPDATE的），MySQL会再次读取这些记录并且这次 InnoDB 要么锁定它 要么 等待它的锁。



这里看一个官方示例。


```sql
CREATE TABLE t (a INT NOT NULL, b INT) ENGINE = InnoDB;
INSERT INTO t VALUES (1,2),(2,3),(3,2),(4,3),(5,2);
COMMIT;
```
在这个示例中， 表时没有索引的，所以查询或者索引扫描时，会采用[隐藏的集群索引](https://dev.mysql.com/doc/refman/8.0/en/innodb-index-types.html)来锁住记录行。


Session A 开启事务，并执行 UPDATE 操作：

```sql
# Session A
START TRANSACTION;
UPDATE t SET b = 5 WHERE b = 3;  # 没有用到索引会全表扫描，所有记录都会锁住
```

此时，session A 事务并未结束， 在另一个 Session B 中执行 UPDATE操作：
```sql
# Session B
UPDATE t SET b = 4 WHERE b = 2;
```

因为 InnoDB 执行每一个 UPDATE 操作，首先会为每一行记录获取可执行锁，然后才决定是否去更新。 如果 InnoDB 没有更新记录行，就会释放锁。 否则， InnoDB 会持有锁直到事务结束。这个示例影响事务如下：

当使用 REPEATABLE READ 级别时， 第一个 UPDATE 获取到了每一行的 x-lock， 事务A 读取并且没有释放它们：

```sql
x-lock(1,2); retain x-lock
x-lock(2,3); update(2,3) to (2,5); retain x-lock
x-lock(3,2); retain x-lock
x-lock(4,3); update(4,3) to (4,5); retain x-lock
x-lock(5,2); retain x-lock
```
此时事务B进行UPDATE，尝试进行锁，等待...

```sql
x-lock(1,2); block and wait for first UPDATE to commit or roll back
```

而如果是 READ COMMITED 级别，事务A获得了锁并且会释放那些不匹配的行记录：

```sql
x-lock(1,2); unlock(1,2)
x-lock(2,3); update(2,3) to (2,5); retain x-lock
x-lock(3,2); unlock(3,2)
x-lock(4,3); update(4,3) to (4,5); retain x-lock
x-lock(5,2); unlock(5,2)
```

事务B的UPDATE操作时，事务B会采取“半一致性”读方式，会读取最近已经提交的版本记录，决定哪些是匹配的：

```sql
x-lock(1,2); update(1,2) to (1,4); retain x-lock
x-lock(2,3); unlock(2,3)
x-lock(3,2); update(3,2) to (3,4); retain x-lock
x-lock(4,3); unlock(4,3)
x-lock(5,2); update(5,2) to (5,4); retain x-lock
```

不管怎样，如果where条件包含一个索引列，InnoDB 就会使用索引，只有索引列会考虑持有锁。 在下面的例子中，事务A的 UPDATE 操作持有每一个b=2的记录的 x-lock 锁。 事务B会阻塞因为它也试图获得相同记录的锁。
```sql
CREATE TABLE t (a INT NOT NULL, b INT, c INT, INDEX (b)) ENGINE = InnoDB;
INSERT INTO t VALUES (1,2,3),(2,2,4);
COMMIT;

# Session A
START TRANSACTION;
UPDATE t SET b = 3 WHERE b = 2 AND c = 3;

# Session B  会阻塞
UPDATE t SET b = 4 WHERE b = 2 AND c = 4;
```





设置事务隔离级别为读提交：

```shell
SET TRANSACTION ISOLATION LEVEL READ COMMITED;
```

事务B提交后，事务A才可能读取到事务B的数据
灭有脏读，但是可能幻读、不可重复读
同一个事务中多次查询结果可能是不一致的。
示例：
事务A先读取id为1的姓名为xiaoming；
事务B提交改为lilei；
事务A再次读取时读到lilei。
这样事务A两次读取的数据不一致，出现了幻读，不可重复读。按业务来讲，事务A应该第二次读取到的是xiaoming，因为事务A可能在第一次读到xiaoming时已经利用它实现了一些业务操作了。所以出现了不可重复读。

#### 可重复读 REPEATABLE READ

```shell
set tx_isolation = 'repeatable-read';
或者
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
```

Mysql InnoDB默认隔离级别。
可以解决上述的案例问题。同一个事务中多次查询结果是一致的。
没法解决幻读。


示例：
事务A读取 薪水是2000，事务B改为3000并提交事务，
事务A再次读取还是2000；
<font color=red>事务A将薪水翻倍改为salary*2以为变成了4000，结果事务A查询发现是6000了。如果使用的是set salary=4000，那就是4000(所以这是一种错误的方式)。</font> 事务A拿到了事务B的数据，产生了幻读。

演示幻读： 事务A作更新后，再次查询就出现了幻读。

【select读取的是快照版本，更新读取的是真实数据。】

MVVC机制： 多版本并发控制机制，使用快照版本数据达到可重复读，但是快照版本是不可信的，无法解决幻读问题。

#### 可串行化 SERIALIZABLE

```shell
set tx_isolation = 'SERIALIZABLE';
```

效率低，可以解决脏读、不可重复读、幻读；
因为它将事务串行化了，没有了并发事务。



## [Mysql间隙锁](https://dev.mysql.com/doc/refman/8.0/en/glossary.html#glos_gap_lock)

也叫[Gap lock](https://dev.mysql.com/doc/refman/8.0/en/glossary.html#glos_gap_lock)。

事务A使用范围update时，假如表中只有id1-11的数据，mysl会对这些记录比如update table where id > 10 and id <20；会对id为11--19的数据都加锁，即便此时数据库并没有这些id为12--19的记录；
此时其他事务B来插入这样的记录insert id 为12 时，则事务B会等待，因为id12被事务A锁了。

## 死锁

事务A：select * from table where id = 1 for update;

事务B：select * from table where id = 2 for update;

事务A：select * from table where id = 2 for update;

事务B：select * from table where id = 1 for update;

查看死锁日志：

```shell
show engine innodb status\G 
```



延伸阅读：

- [XA事务](https://dev.mysql.com/doc/refman/8.0/en/glossary.html#glos_xa)