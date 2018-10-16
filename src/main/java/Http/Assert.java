package Http;

import org.junit.rules.ErrorCollector;
import static org.hamcrest.Matchers.*;

/***
 * 封装常用断言
 */


public class Assert {

    private final ErrorCollector collector;

    public Assert(ErrorCollector collector) {
        this.collector = collector;
    }

    public  void assertEqual(Object check,Object expect){
        this.collector.checkThat(check, equalTo(expect));
    }

    public  void assertNoteuals(Object check,Object expect){
        this.collector.checkThat(check,not(expect));
    }

    public  void assertLessthan(int check,int expect){
        this.collector.checkThat(check, lessThan(expect));
    }

    public  void assertGreaterthan(int check,int expect){
        this.collector.checkThat(check, greaterThan(expect));
    }

}
