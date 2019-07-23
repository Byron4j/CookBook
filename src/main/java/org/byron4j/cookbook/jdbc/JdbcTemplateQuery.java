package org.byron4j.cookbook.jdbc;

import org.byron4j.cookbook.util.C3P0Utils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * <pre>
 * 演示JdbcTemplate关于查询的API使用方法
 *      1. 查询单行
 *      2. 查询多行
 *      3. 查询单个值
 * </pre>
 * @program: web19
 * @author: Byron
 * @create: 2019/07/23
 */
public class JdbcTemplateQuery {

    @Test
    @Ignore
    public void 查询单条记录() throws SQLException {
        Connection connection = C3P0Utils.getConnection();
        String s = "select * from user limit 1";
        PreparedStatement preparedStatement = connection.prepareStatement(s);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            System.out.println(resultSet.getString("username"));
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(JdbcTemplateQuery.class.getClassLoader().getResource(""));
        InputStream resourceAsStream = JdbcTemplateQuery.class.getClassLoader().getResourceAsStream("c3p0.properties");
        Properties p = new Properties();
        p.load(resourceAsStream);
        System.out.println(p);
    }

}
