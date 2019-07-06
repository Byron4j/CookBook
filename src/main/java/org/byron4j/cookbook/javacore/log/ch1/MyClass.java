package org.byron4j.cookbook.javacore.log.ch1;

import java.util.logging.Logger;

/**
 * JCL:java.util.logging.Logger
 */
public class MyClass {
    private static Logger LOGGER ;

    static {

        System.setProperty("java.util.logging.config.file", "F:\\217my_optLogs\\001系统相关\\系统设计\\007\\CookBook\\src\\main\\resources\\logging.properties");

        //must initialize loggers after setting above property
        LOGGER = Logger.getLogger(MyClass.class.getName());
    }

    public static void main(String[] args) {
        System.out.println("main method starts");
        LOGGER.info("info msg");
        LOGGER.warning("warning msg");
    }
}
