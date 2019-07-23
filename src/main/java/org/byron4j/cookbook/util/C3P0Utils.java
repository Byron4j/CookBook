package org.byron4j.cookbook.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <pre>
 *     1. 读取数据源--仅仅读取一次
 *     2. 获取链接
 *     3. 释放资源---不是真的关闭连接，而是回归到连接池中
 * </pre>
 * @program: jdbc02
 * @author: Byron
 * @create: 2019/07/22
 */
public class C3P0Utils {
    // 初始化数据源--默认自动读取类路径下的 文件配置信息
    private static DataSource dataSource = new ComboPooledDataSource();

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void releaseConnection(ResultSet resultSet, Statement statement, Connection connection){
        try{
            if( resultSet != null ){
                resultSet.close();
            }
            if( statement != null ){
                statement.close();
            }
            if( connection != null ){
                // 不同的实现，不同的关闭逻辑
                // 来自于连接池则是回收；来自于直接创建则是直接销毁
                connection.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**获取数据源*/
    public static DataSource getDataSource(){
        return dataSource;
    }
}
