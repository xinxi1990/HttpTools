package Http;

import com.relevantcodes.extentreports.ExtentReports;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.notification.Failure;
//import static Tools.MyLogger.log_info;

public class Run {

    public static String FILEPATH;
    public static String REPORTPATH;
    public  static String  LEVEL;
    private static boolean NEEDHELP = false;
    private static ExtentReports extent;

    public static void main(String[] args) throws Exception {
        executeParameter(args);
    }

    /**
     * 执行参数
     * @param args
     * @throws Exception
     */
    private static void executeParameter(String[] args) throws Exception {
        int optSetting = 0;

        for (; optSetting < args.length; optSetting++) {
            if ("-f".equals(args[optSetting])) {
                FILEPATH = args[++optSetting];
            }else if ("-r".equals(args[optSetting])) {
                REPORTPATH = args[++optSetting];
            } else if ("-v".equals(args[optSetting])) {
                LEVEL= args[++optSetting];
            }else if ("-h".equals(args[optSetting])) {
                NEEDHELP = true;
//                log_info("-f:测试用例路径\n");
//                log_info("-r:报告文件夹\n");
//                log_info("-v:日志等级\n");
                break;
            }
        }
        if (!NEEDHELP) {
            try {
                //log_info("-f:测试用例路径\n");
            } catch (Exception e) {
                //log_info("请确认参数配置,需要帮助请输入 java -jar HttpTools.jar -h\n"
               //         + "ERROR信息"+ e.toString());
            }
        }

        System.setProperty("FILEPATH",FILEPATH);
        System.setProperty("REPORTPATH",REPORTPATH);
        new JUnitCore().run(Request.method(Requests.class, "run"));
         //测试类的class对象
//        for (Failure failure : result.getFailures()) {
//            //对于执行失败的情况打印失败信息
//            System.out.println(failure.toString());
//
//        }

    }

}

