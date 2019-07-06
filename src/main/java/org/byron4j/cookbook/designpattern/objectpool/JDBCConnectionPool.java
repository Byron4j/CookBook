package org.byron4j.cookbook.designpattern.objectpool;

import java.sql.Connection;
import java.sql.DriverManager;

public class JDBCConnectionPool extends ObjectPool<Connection> {

    private String dsn, username, pwd;

    public JDBCConnectionPool(String driver, String dsn, String username, String pwd) {
        super();
        System.out.println("获取JDBC连接池.");
        try{
            Class.forName(driver).newInstance();
        }catch (Exception e){
            e.printStackTrace();;
        }

        this.dsn = dsn;
        this.username = username;
        this.pwd = pwd;
    }

    @Override
    protected Connection create() {
        System.out.println("创建新的连接.");
        try{
            return DriverManager.getConnection(dsn, username, pwd);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean validate(Connection o) {
        System.out.println("检查连接是否有效.");
        try{
            return !o.isClosed();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void expire(Connection o) {
        System.out.println("关闭连接.");
        try{
            o.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
