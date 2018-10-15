package Http;

import DataModel.StepModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import java.io.File;
import java.io.IOException;
import java.util.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class Requests {

    @Rule
    public ErrorCollector collector = new ErrorCollector();
    public static List<StepModel> testcase;
    public static Response response;
    public String checkKey;
    public Object checkValue;
    public String selectkey;
    public Object check;
    public Object exp;

    /**
     * @param filePath
     * @return
     * @throws IOException
     */
    public static List<StepModel> load(String filePath) throws IOException {
        System.out.println(filePath);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        List<StepModel> data = mapper.readValue(
                new File(filePath),
                new TypeReference<List<StepModel>>(){}
        );
        return data;
    }


    @BeforeClass
    public static void beforeClass() throws IOException {
        String yamlPath = System.getProperty("user.dir") + "/src/main/java/Data/post_temp.yaml";
        testcase = load(yamlPath);
    }


    @Test
    public void run(){
        RestAssured.useRelaxedHTTPSValidation();
        for( StepModel step: testcase){
            System.out.println(step.info.name);
            if(step.given.request.equals("get")) {
                if (step.given.queryParam == null && step.given.headers == null){
                    response = (Response) given().when().log().all().get(step.when.url).then().extract();
                }else {
                    Map queryParam = (Map) step.given.queryParam;
                    Map headers = (Map) step.given.headers;
                    response = (Response) given().headers(headers).params(queryParam).when().log().all().get(step.when.url).then().extract();
                }
                //collector.checkThat(response.statusCode(), equalTo(step.then.statusCode));
            }else if (step.given.request.equals("post")){
                Map body = (Map) step.given.body;
                Map headers = (Map) step.given.headers;
                response = (Response) given().headers(headers)
                        .body(body).when().log().all()
                        .post(step.when.url).then().extract();
                collector.checkThat(response.statusCode(), equalTo(step.then.statusCode));
                System.out.println("断言接口状态码");
                List getBody = (List) step.then.body;
                for (Object bd:getBody) {
                    JSONObject object = JSONObject.fromObject(bd);
                    Iterator<String> sIterator = object.keys();
                    while(sIterator.hasNext()){
                        selectkey = sIterator.next();
                        System.out.println("断言类型:" + selectkey);
                        JSONArray aslist = object.getJSONArray(selectkey);
                        System.out.println("断言数据列表:" + aslist);
                        checkKey = (String) aslist.get(0);
                        exp =  aslist.get(1);
                        check = response.path(checkKey);
                        System.out.println("响应解析的值:" + check);
                        selectAssert(selectkey,check,exp);
                    }
                }
            }
        }
    }



    /**
     * 根据key选择不同类型的断言方法
     * @param key
     * @param check
     * @param expect
     */
    public void selectAssert(String key,Object check,Object expect) {
        System.out.println("实际值:" + check);
        System.out.println("预期值:" + expect);
        Assert as = new Assert(collector);
        if (key.equals("eq")) {
            as.assertEqual(check, expect);
        } else if (key.equals("nq")) {
            as.assertNoteuals(check, expect);
        }
    }

}
