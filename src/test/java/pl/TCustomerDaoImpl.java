package pl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.orm.Customer;

/**
 * Created by wangpeng on 2016/5/8.
 */
public class TCustomerDaoImpl /*implements TCustomerDao*/ {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public Customer selectByCustomerIdAndAgentCode(Long customerId, String agentCode) {
        return null;
    }

    public Customer selectTitleAndStatusAndMobileByCustomerIdAndAgentCode(Long customerId, String agentCode) {
        return null;
    }
}
