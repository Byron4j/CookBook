package org.byron4j.cookbook.springMVC.root.repo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.byron4j.cookbook.springMVC.root.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * 测试带参数的Dao
 */
@Repository
public class NamedParameterJdbcTemplateDao {

    protected static final Log logger = LogFactory.getLog(NamedParameterJdbcTemplateDao.class);

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public User getInfo(Long id){

        logger.info("查询id为:" + id);

        String sql = "select * from user where id = :paramId";

        SqlParameterSource namedParameters = new MapSqlParameterSource("paramId", id);

        User  user = namedParameterJdbcTemplate.queryForObject(
                sql, namedParameters, new BeanPropertyRowMapper<User>(User.class));

        return user;

    }

}
