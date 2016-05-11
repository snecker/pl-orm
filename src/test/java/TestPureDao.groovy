import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import pl.orm.Customer
import pl.orm.TCustomerDao
/**
 * Created by wangpeng on 2016/5/9.
 */
class TestPureDao extends BaseTest {
    @Autowired
    TCustomerDao tCustomerDao;

    @Test
    public void testDao() throws Exception {
//        Customer cus = tCustomerDao.selectBy(23, "testwanglu")
//        println "${cus == null ? null : ToStringBuilder.reflectionToString(cus)}"
//
//        Customer cus1 = tCustomerDao.selectCustomerIdAndContactsBy(23, "testwanglu")
//        println "${ToStringBuilder.reflectionToString(cus1)}"

//        Map param = [customerId: 23, agentCode: 'testwanglu']
//        def cus2 = tCustomerDao.selectCustomerIdAndContactsBy(param)

        Customer param = new Customer()
        param.setCustomerId(23)
        param.setAgentCode("testwanglu")
        def cus2 = tCustomerDao.selectCustomerIdAndContactsBy(param)
        println("${cus2}")
    }

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate

    @Test
    public void testName() throws Exception {
//        assert namedParameterJdbcTemplate != null;
        QueryCus queryCus = new QueryCus();
        queryCus.setCustomerId(23L)
        SqlParameterSource sql = new BeanPropertySqlParameterSource(queryCus)
        Customer cus = namedParameterJdbcTemplate.query("select * from t_customer where customer_id =:customerId", sql, new BeanPropertyRowMapper(Customer.class)).get(0)
        println "${cus}"

    }

    class QueryCus {
        private Long customerId;
        private String csCustomerId;

        String getCsCustomerId() {
            return csCustomerId
        }

        void setCsCustomerId(String csCustomerId) {
            this.csCustomerId = csCustomerId
        }

        Long getCustomerId() {
            return customerId
        }

        void setCustomerId(Long customerId) {
            this.customerId = customerId
        }
    }
}
