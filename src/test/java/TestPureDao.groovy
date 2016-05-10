import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
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
        Customer cus = tCustomerDao.selectByCustomerIdAndAgentCode(23, "testwanglu")

        println "${cus == null ? null : cus.getContacts()}"


    }
}
