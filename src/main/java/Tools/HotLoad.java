package Tools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 热加载
 * @author xinxi
 */


public class HotLoad {

    public static void main(String[] args) throws Exception {
        List valuelist = new ArrayList();
        valuelist.add(3);
        final int valuesize =  valuelist.size();
        Object[] value = valuelist.toArray(new Object[valuesize]);
        List classlist = new ArrayList();
        classlist.add(double.class);
        final int size =  classlist.size();
        Class[] parameterTypes = (Class[])classlist.toArray(new Class[size]);
        HashMap hashMap = new HashMap();
        hashMap.put("parameter",parameterTypes);
        hashMap.put("value",value);
        reflectMethod("Tools.FileUntils","sleep",hashMap);
    }


    public static void reflectMethod(String className,String methodName,HashMap hashMap) throws Exception {
        Class<?> clz = Class.forName(className);
        Object o = clz.newInstance();
        Class[] parameterTypes = (Class[]) hashMap.get("parameter");
        Method m = clz.getMethod(methodName, parameterTypes);
        System.out.println(String.valueOf(hashMap.get("value")));
        m.invoke(o,hashMap.get("value"));

    }



}



