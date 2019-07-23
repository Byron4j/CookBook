package org.byron4j.cookbook.dbconnpool.c3p0;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @program: cookbook
 * @author: Byron
 * @create: 2019/07/19
 */
public class Ch01 {
    public static void main(String[] args) {
        ComboPooledDataSource dcp = new ComboPooledDataSource();
        try {
            dcp.setDriverClass("com.mysql.jdbc.Driver");
            dcp.setJdbcUrl("jdbc:mysql://localhost/mydb");
            dcp.setUser("root");
            dcp.setPassword("root");
            Connection connection = dcp.getConnection();
            Statement statement = connection.createStatement();
            String sql = "select * from student";
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                System.out.println(resultSet.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
