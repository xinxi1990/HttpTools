package Http;

import DataModel.StepModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Requests {

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    public static List<StepModel> testcase;

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
        String yamlPath = System.getProperty("user.dir") + "/src/main/java/Data/temp.yaml";
        testcase = load(yamlPath);
    }


    @Test
    public void run(){
        RestAssured.useRelaxedHTTPSValidation();
        for( StepModel step: testcase){
            System.out.println(step.info.name);
            if(step.given.request.equals("get")) {
                Map queryParam = (Map) step.given.queryParam;
                Map headers = (Map) step.given.headers;
                Response response = (Response) given().headers(headers).params(queryParam).when().log().all().get(step.when.url).then().extract();
            }else if (step.given.request.equals("post")){
                Map body = (Map) step.given.body;
                Map headers = (Map) step.given.headers;
                Response response = (Response) given().headers(headers).body(body).when().log().all().post(step.when.url).then().extract();
                Map responseBody = (Map) step.then.body;
                System.out.println(responseBody);
                collector.checkThat(response.statusCode(), equalTo(step.then.statusCode));
                Iterator entries = responseBody.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    System.out.println("Key = " + key);
                    System.out.println("Value = " + value);
                    collector.checkThat(response.statusCode(), equalTo(step.then.statusCode));
                }


            }
        }
    }



}
