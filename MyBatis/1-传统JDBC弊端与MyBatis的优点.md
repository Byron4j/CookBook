# 传统JDBC弊端与MyBatis的优点

## 传统JDBC弊端

- 没有使用连接池，会频繁创建与关闭数据库连接
- 编码麻烦
- PreparedStatement预编译进行变量赋值需要编写数字1、2、3...，不易于开发实践
- 对于处理返回结果ResultSet需要硬编码

## MyBatis介绍

- MyBatis是一个ORM（对象关系映射）框架。
- 支持XML配置。
- 支持注解配置。

