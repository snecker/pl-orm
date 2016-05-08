import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangpeng on 2016/5/7.
 */
public class TestReflection {

    public List<String> getList(String name, List<String> list, int val1, Integer val2) {
        return Arrays.asList("1", "2");
    }

    @Test
    public void test() throws Exception {

        Class<TestReflection> cc = TestReflection.class;

        Method[] methos = cc.getDeclaredMethods();

        for (Method m : methos) {
            System.out.println("" + m.getName());
        }
    }
}
