import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Created by wangpeng on 2016/5/10.
 */
public abstract class TestB {
    public void test1(String param1, Long param2) {
        System.out.println("param1");
    }

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

}
