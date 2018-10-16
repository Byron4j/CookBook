# MySQL锁机制、事务隔离级别概览

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


第一步：在session1中对表user进行加读锁：

session1：

```shell
mysql> lock table user read;
Query OK, 0 rows affected (0.00 sec)
```

第二步：session2进行读、写操作：

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


第三步：session中进行读没问题，在session1中进行更新操作会直接报错，提示<font color=red>表存在读锁不能被更新</font>：

```shell
mysql> insert into `user` ( `name`, `age`, `salary`, `version`) values('唐玄奘','40','8000.00','0');
ERROR 1099 (HY000): Table 'user' was locked with a READ lock and can't be updated
```


第四步： 释放session1中的读锁

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

**读锁总结**： <u>对表加读锁，不影响任何session的读操作，都是共享的。 但是当前session执行更新操作会失败报错，其他session更新则会阻塞等待。因为会阻塞等待，所以读锁是悲观锁。</u>




#### 写锁示范





# 行级锁


## 事务

命令行演示，使用begin、commit演示多个session之间的事务

A：原子性，全部执行全部不执行
C：一致性，开始和完成时数据状态一致
I：隔离性，事务之间隔离
D：持久性，事务完成是永久的


## 并发带来的问题

####更新丢失


####脏读
事务A读取到了事务B修改的数据，但是事务B还未提交，事务B失败回滚，则事务A读取的无效，不符合一致性

####幻读
事务A多次读取，后面读取读到了其他事务的提交新增的数据，导致查询结果前后不一，比如开始查询结果是1条，后面查询是2条。不符合隔离性

####不可重复读

事务A多次读取，后面读取读到了其他事务的提交，导致查询的数据已经变化。
比如开始读取id为1的姓名是xiaoming，后面再次读取发现姓名是lilei。不符合隔离性。

## 事务隔离级别

Mysql默认是可重复读。

- Mysql查看隔离级别命令
```shell
## 查看隔离级别
show variables like 'tx_isolation'
```


#### 读未提交

```shell
set tx_isolation = 'read-uncommited';
```

事务A可以读取事务B未提交的事务，会出现脏读。

#### 读已提交

```shell
set tx_isolation = 'read-commited';
```

事务B提交后，事务A才可能读取到事务B的数据
灭有脏读，但是可能幻读、不可重复读
同一个事务中多次查询结果可能是不一致的。
示例：
事务A先读取id为1的姓名为xiaoming；
事务B提交改为lilei；
事务A再次读取时读到lilei。
这样事务A两次读取的数据不一致，出现了幻读，不可重复读。按业务来讲，事务A应该第二次读取到的是xiaoming，因为事务A可能在第一次读到xiaoming时已经利用它实现了一些业务操作了。所以出现了不可重复读。

#### 可重复读

```shell
set tx_isolation = 'repeatable-read';
```

Mysql默认隔离级别。
可以解决上述的案例问题。同一个事务中多次查询结果是一致的。
没法解决幻读。


示例：
事务A读取 薪水是2000，事务B改为3000并提交事务，
事务A再次读取还是2000；
<font color=red>事务A将薪水翻倍改为salary*2以为变成了4000，结果事务A查询发现是6000了。如果使用的是set salary=4000，那就是4000(所以这是一种错误的方式)。</font> 事务A拿到了事务B的数据，产生了幻读。

演示幻读： 事务A作更新后，再次查询就出现了幻读。

【select读取的是快照版本，更新读取的是真实数据。】

MVVC机制： 多版本并发控制机制，使用快照版本数据达到可重复读，但是快照版本是不可信的，无法解决幻读问题。

#### 可串行化

```shell
set tx_isolation = 'SERIALIZABLE';
```

效率低，可以解决脏读、不可重复读、幻读；
因为它将事务串行化了，没有了并发事务。



## Mysql 间隙锁

Gap lock。

事务A使用范围update时，假如表中只有id1-11的数据，mysl会对这些记录比如update table where id > 10 and id <20；会对id为11--19的数据都加锁，即便没有这些12-19的记录；
此时其他事务来插入这样的记录insert id 为12 时，则事务B会等待，因为id12被锁了。

## 死锁

事务A：select * from table where id = 1 for update;

事务B：select * from table where id = 2 for update;

事务A：select * from table where id = 2 for update;

事务B：select * from table where id = 1 for update;

查看死锁日志：

```shell
show engine innodb status\G 
```
