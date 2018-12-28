package org.byron4j.cookbook.designpattern;

import org.byron4j.cookbook.designpattern.objectpool.JDBCConnectionPool;
import org.junit.Test;

import java.sql.Connection;

public class ObjectPoolTest {

    @Test
    public void test(){

        // 创建JDBC连接池
        JDBCConnectionPool pool = new JDBCConnectionPool(
                "com.mysql.jdbc.Driver", "jdbc:mysql://localhost/mypydb",
                "root", "11111111");

        // 从连接池中获取一个连接
        Connection con = pool.checkout();


        // 连接回收
        pool.checkin(con);
    }
}
