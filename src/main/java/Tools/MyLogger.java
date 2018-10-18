package Tools;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.log4j.Logger;

public class MyLogger {

    public static Logger logger;
    private ExtentReports extent;
    private ExtentTest test;

    public MyLogger(ExtentReports extent,ExtentTest test) {
        this.extent = extent;
        this.test = test;
    }
    /**
     * 初始化logger
     */
    public static Logger initLogger(){
        logger = Logger.getLogger("HttpTools");
        return logger;
    }

    /**
     * info日志
     */
    public  void log_info(String text){
        test.log(LogStatus.INFO, text);
        initLogger().info(text);

    }

    /**
     * debug日志
     */
    public  void log_debug(String text){
        initLogger().info(text);
    }





}
