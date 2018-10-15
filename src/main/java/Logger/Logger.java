package Logger;

import org.testng.Reporter;

public class Logger {

    /**
     * 初始化logger
     */
    public static org.testng.log4testng.Logger logger = org.testng.log4testng.Logger.getLogger(Logger.class);


    /**
     * info日志
     */
    public static void log_info(String text){
        Reporter.log(text);
        logger.info(text);
    }

    /**
     * debug日志
     */
    public static void log_debug(String text){
        Reporter.log(text);
        logger.info(text);
    }

    /**
     * trace日志
     */
    public static void log_trace(String text){
        Reporter.log(text);
        logger.trace(text);
    }



    public static void main(String[] args) {
        log_info("TEST");
    }

}