package Http;

import DataModel.StepModel;
import DataModel.ThenModel;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.log4j.Level;
import org.junit.Rule;
import org.junit.Test;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.BeforeClass;
import java.util.*;
import java.io.File;
import java.io.IOException;
import io.restassured.RestAssured;
import org.junit.rules.ErrorCollector;
import io.restassured.response.Response;
import static Tools.MyLogger.initLogger;
import static Tools.MyLogger.log_info;
import static io.restassured.RestAssured.filters;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;


public class Requests {

    @Rule
    public ErrorCollector collector = new ErrorCollector();
    public static List<StepModel> testcase;
    public static Response response;
    public static Response newResponse;
    public String checkKey;
    public Object checkValue;
    public String selectkey;
    public Object check;
    public Object exp;
    public String ContentTypeJson = "application/json";
    public String ContentTypeFrom = "application/x-www-form-urlencoded";
    public static ResponseSpecBuilder rb= new ResponseSpecBuilder();
    public static ResponseSpecification rs;

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

    public static void globalBuilder(){
        rb.expectStatusCode(200);
        rb.expectResponseTime(lessThan(1000L));
        rs = rb.build();
    }




    public static void responseFilters(){
        filters((new Filter() {
                    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
                        Response response = ctx.next(requestSpec, responseSpec);
                        System.out.println(response.getBody().asString());
                        Response newResponse = new ResponseBuilder().clone(response)
                                .setContentType(ContentType.JSON)
                                .build();
                        log_info("response filter");
                        return newResponse;
                    }
                })
        );
    }



    @BeforeClass
    public static void beforeClass() throws IOException {
        initLogger().setLevel(Level.ALL);
        String yamlPath = System.getProperty("user.dir") + "/src/main/java/Data/post_temp.yaml";
        testcase = load(yamlPath);
        RestAssured.useRelaxedHTTPSValidation();
        responseFilters();
    }








    @Test
    public void run(){
        for( StepModel step: testcase){
            log_info(step.info.name);
            if(step.given.request.equals("get")) {
                Map queryParam = (Map) step.given.queryParam;
                Map headers = (Map) step.given.headers;
                if (step.given.queryParam == null && step.given.headers == null){
                    response = (Response) given().when().log().all().get(step.when.url).then().extract();
                }else if (step.given.queryParam == null){
                    response = (Response) given().headers(headers).when().log().all().get(step.when.url).then().extract();
                }else {
                    response = (Response) given().headers(headers).params(queryParam).when().log().all().get(step.when.url).then().extract();
                }
            }else if (step.given.request.equals("post")){
                Map body = (Map) step.given.body;
                Map headers = (Map) step.given.headers;
                String ContentType = (String) headers.get("Content-Type");
                System.out.println(ContentType);
                if (ContentType.equals(ContentTypeFrom)){
                    response = (Response) given().headers(headers)
                            .formParams(body).when().log().all()
                            .post(step.when.url).then().extract();
                } else if (ContentType.equals(ContentTypeJson)){
                    response = (Response) given().headers(headers)
                            .body(body).when().log().all()
                            .post(step.when.url).then().extract();
                }
            }
            getResponse(response,step);

        }
    }



    public void getResponse(Response response, StepModel step){
        selectAssert("eq",response.statusCode(),step.then.statusCode);
        log_info("断言接口状态码");
        List getBody = (List) step.then.body;
        for (Object bd:getBody) {
            JSONObject object = JSONObject.fromObject(bd);
            Iterator<String> sIterator = object.keys();
            while(sIterator.hasNext()){
                selectkey = sIterator.next();
                log_info("断言类型:" + selectkey);
                JSONArray aslist = object.getJSONArray(selectkey);
                log_info("断言数据列表:" + aslist);
                checkKey = String.valueOf(aslist.get(0)) ;
                exp =  aslist.get(1);
                check = response.getBody().jsonPath().getString(checkKey);
                log_info("响应解析的值:" + check);
                selectAssert(selectkey,check,exp);
            }
        }
    }


    /**
     * 选择不同类型的断言方法
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
