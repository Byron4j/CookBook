package org.byron4j.cookbook.web;

import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.byron4j.cookbook.springMVC.root.repo.JdbcCorporateEventDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController("hello")
public class HelloController {

    protected static final Log logger = LogFactory.getLog(HelloController.class);

    @Autowired
    JdbcCorporateEventDao jdbcCorporateEventDao;

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
        logger.info(jdbcCorporateEventDao.getInfo());
        return localDate.toString();
    }
}
