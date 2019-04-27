package org.byron4j.cookbook.web;

import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.byron4j.cookbook.springMVC.root.domain.User;
import org.byron4j.cookbook.springMVC.root.repo.JdbcTemplateDao;
import org.byron4j.cookbook.springMVC.root.repo.NamedParameterJdbcTemplateDao;
import org.byron4j.cookbook.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController("hello")
public class HelloController {

    protected static final Log logger = LogFactory.getLog(HelloController.class);

    @Autowired
    JdbcTemplateDao jdbcTemplateDao;

    @Autowired
    NamedParameterJdbcTemplateDao namedParameterJdbcTemplateDao;

    @ModelAttribute
    LocalDate initLocalDate() {
        return LocalDate.now();
    }

    /**
     * http://localhost:8080/hello?name=oyy will return "{"oyy":"Hello"}" to the page
     * @param name
     * @return
     */
    @RequestMapping("/hello")
    public String sayHello(@RequestParam  String name){
        JSONObject obj = new JSONObject();
        obj.put(name, "Hello");
        return obj.toString();
    }

    @RequestMapping("/getDate")
    public String getDate(@ModelAttribute LocalDate localDate){

        List<User> users = jdbcTemplateDao.getInfo();

        logger.info(users);

        logger.info("========================================");
        logger.info(namedParameterJdbcTemplateDao.getInfo(users.get(0).getId()));

        logger.info("========================================");

        logger.info("获取新插入的User的自增主键值:" + jdbcTemplateDao.insertUser("嬴政" + Utils.random()));
        return localDate.toString();
    }
}
