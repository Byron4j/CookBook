package org.byron4j.cookbook.springMVC.root.repo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.byron4j.cookbook.springMVC.root.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 测试Dao
 */
@Repository
public class JdbcTemplateDao {

    protected static final Log logger = LogFactory.getLog(JdbcTemplateDao.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<User> getInfo(){
        logger.info("获取user表信息开始:");
        return jdbcTemplate.query(
                "select * from user",
                new RowMapper<User>() {
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return User.builder()
                                .email(rs.getString("email"))
                                .id(Long.valueOf(rs.getString("id")))
                                .nickName(rs.getString("nick_name"))
                                .build();
                    }
                }
        );
    }

    public Long insertUser(String nickName){
        final String INSERT_SQL = "insert into User (nick_name) values(?)";
        final String name = nickName;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[] {"id"});
                        ps.setString(1, name);
                        return ps;
                    }
                },
                keyHolder);

        // 获取插入后的自增主键值
        return keyHolder.getKey().longValue();
    }

}
