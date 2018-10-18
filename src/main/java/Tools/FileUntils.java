package Tools;

import com.google.gson.Gson;
import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.util.Calendar;
import java.util.Map;



/**
 * 文件工具类
 * @author xinxi
 */


public class FileUntils {


    /**
     * 读Yaml文件
     * param YamlName
     * return Map类型
     */
    public static Map readYaml(String yamlpath)
    {
        Map<String, String> map = null;
        try{
            File dumpFile = new File(yamlpath);
            Yaml yaml = new Yaml();
            map = yaml.load((new FileInputStream(dumpFile)));
        }catch (Exception e){
            System.out.println(String.format("读取%s异常! + \n + %s", yamlpath,e));
        }
        return map;
    }




    /**
     * 获取当前时间
     * return timeStr
     */
    public static String currentTime(){
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        String timeStr = (year + "/" + month + "/" + date + " " +hour + ":" +minute + ":" + second);
        return timeStr;
    }

//    public static String fileRead(String fileName){
//        String result="";
//        try {
//            String encoding = "utf-8";
//            File file = new File(fileName);
//            InputStreamReader read = new InputStreamReader(
//                    new FileInputStream(file), encoding);// 考虑到编码格式
//            BufferedReader bufferedReader = new BufferedReader(read);
//            String lineTxt = null;
//            while ((lineTxt = bufferedReader.readLine()) != null) {
//                result +=lineTxt + "\n";
//            }
//            read.close();
//        } catch (FileNotFoundException e) {
//            log_info("找不到指定文件");
//        }catch (IOException e) {
//            log_info("读取文件内容出错");
//            e.printStackTrace();
//        }
//        return result;
//    }


    public static String yamlToJson(String file) {
        Gson gs = new Gson();
        Map<String, Object> loaded = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            Yaml yaml = new Yaml();
            loaded = (Map<String, Object>) yaml.load(fis);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return gs.toJson(loaded);
    }


    public static void sleep(double second){
        try {
            Thread.sleep((long) (second*1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if(dir.isFile()){
            dir.delete();
        }else{
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteDir(files[i]);
                files[i].delete();
            }
        }
        if(! dir.exists()) dir.mkdir();
        return dir.exists() && dir.list().length == 0;
    }

    public static boolean removeDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static String getFileName(String basePath){
        removeFolder(basePath, -7);
        String name = DataUntils.getNow("yyyyMMdd");
        File file = new File(basePath + File.separator + name);
        if(! file.exists()){
            return file.getAbsolutePath();
        }
        File[] tempList = new File(basePath).listFiles();
        int index = 1;
        int flag = 0;
        for (int i = 0; i < tempList.length; i++) {
            if(tempList[i].isDirectory() && tempList[i].getName().startsWith(name)){
                if(tempList[i].getName().contains("_")){
                    int pos = tempList[i].getName().indexOf("_");
                    pos = Integer.parseInt(tempList[i].getName().substring(pos+1));
                    if(pos > index){
                        index = pos;
                    }
                    flag++;
                }
            }
        }
        if(flag > 0) index++;
        return basePath + File.separator + name + "_" + index;
    }

    public static void removeFolder(String basePath, int day){
        String endDate = DataUntils.getNewDate(day);
        File baseFile = new File(basePath);
        if(baseFile.exists()){
            for (File file : baseFile.listFiles()) {
                if(file.isDirectory() && file.getName().startsWith(endDate)){
                    removeDir(file);
                }
            }
        }
    }



}
