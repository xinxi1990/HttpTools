package Http;

import Model.StepModel;
import Tools.ExtentUtils;
import Tools.MyLogger;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.NetworkMode;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.log4j.Level;
import org.hamcrest.Matchers;
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
import static io.restassured.RestAssured.filters;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import static Tools.DataUntils.timeDate;

public class Requests {

    @Rule
    public ErrorCollector collector = new ErrorCollector();
    public static List<StepModel> testcase;
    public static Response response;
    public String checkKey;
    public String selectkey;
    public Object check;
    public Object exp;
    public String ContentTypeJson = "application/json";
    public String ContentTypeFrom = "application/x-www-form-urlencoded";
    public static RequestSpecBuilder rsb = new RequestSpecBuilder();
    public static ResponseSpecBuilder rb= new ResponseSpecBuilder();
    public static ResponseSpecification rs;
    public  static String CASEPATH;
    public  static String  LEVEL = "ALL";
    private static ExtentReports extent;
    private static String reportPath = String.format( System.getProperty("REPORTPATH")
            + "/reports/report_%s.html", timeDate());
    public static MyLogger logger;
    public static ExtentTest extentTest;


    @Rule
    public ExtentUtils eu = new ExtentUtils(extent,extentTest);

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
    public static void setup() throws IOException {
        CASEPATH = System.getProperty("FILEPATH");
        testcase = load(CASEPATH);
        extent = new ExtentReports(reportPath, true, NetworkMode.OFFLINE);
        extentTest = extent.startTest("test", "-");
        logger = new MyLogger(extent,extentTest);
        logger.log_info("初始化全局参数");
        rb.expectResponseTime(lessThan(1000L));
        rs = rb.build();
        initLogger().setLevel(Level.ALL);
        RestAssured.useRelaxedHTTPSValidation();
        responseFilters();
    }

    /**
     * 响应拦截器
     */
    public static void responseFilters(){
        filters((new Filter() {
                    public Response filter(FilterableRequestSpecification requestSpec,
                                           FilterableResponseSpecification responseSpec, FilterContext ctx) {
                        Response response = ctx.next(requestSpec, responseSpec);
                        Response newResponse = new ResponseBuilder().clone(response)
                                .setContentType(ContentType.JSON)
                                .build();
                        logger.log_info("开启响应拦截器");
                        return newResponse;
                    }
                })
        );
    }





    @Test
    public void run(){
        logger.log_info("开始测试!");
        for( StepModel step: testcase){
            logger.log_info("接口名称:" + step.info.name);
            if(step.given.request.equals("get")) {
                Map queryParam = (Map) step.given.queryParam;
                Map headers = (Map) step.given.headers;
                if (step.given.queryParam == null && step.given.headers == null){
                    response = (Response) given().when().get(step.when.url).then().extract();
                }else if (step.given.queryParam == null){
                    response = (Response) given().headers(headers).when().get(step.when.url).then().extract();
                }else {
                    response = (Response) given().headers(headers).
                            params(queryParam).when().get(step.when.url).then().extract();
                }
            }else if (step.given.request.equals("post")){
                Map body = (Map) step.given.body;
                Map headers = (Map) step.given.headers;
                String ContentType = (String) headers.get("Content-Type");
                if (ContentType.equals(ContentTypeFrom)){
                    response = (Response) given().headers(headers)
                            .formParams(body).when().post(step.when.url).then().extract();
                } else if (ContentType.equals(ContentTypeJson)){
                    response = (Response) given().headers(headers)
                            .body(body).when().post(step.when.url).then().extract();
                }
            }
            getResponse(response,step);
        }
    }


    private void getResponse(Response response, StepModel step){
        org.junit.Assert.assertEquals(response.statusCode(),step.then.statusCode);
        logger.log_info("断言接口状态码成功!");
        List getBody = (List) step.then.body;
        for (Object bd:getBody) {
            JSONObject object = JSONObject.fromObject(bd);
            Iterator<String> sIterator = object.keys();
            while(sIterator.hasNext()){
                selectkey = sIterator.next();
                logger.log_info("断言类型:" + selectkey);
                JSONArray aslist = object.getJSONArray(selectkey);
                logger.log_info("断言数据列表:" + aslist);
                checkKey = String.valueOf(aslist.get(0)) ;
                exp =  aslist.get(1);
                check = response.getBody().jsonPath().getString(checkKey);
                logger.log_info("响应解析的值:" + check);
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
        logger.log_info("实际值:" + check);
        logger.log_info("预期值:" + expect);
        Assert as = new Assert(collector);
        if (key.equals("eq")) {
            as.assertEqual(check, expect);
        } else if (key.equals("nq")) {
            as.assertNoteuals(check, expect);
        }
    }

}
