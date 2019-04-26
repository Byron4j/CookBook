package org.byron4j.cookbook.springMVC.root.repo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.byron4j.cookbook.springMVC.root.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 测试Dao
 */
@Repository
public class JdbcCorporateEventDao{

    protected static final Log logger = LogFactory.getLog(JdbcCorporateEventDao.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public String getInfo(){
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
        ).toString();
    }

}
