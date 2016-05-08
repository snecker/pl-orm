package pl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.orm.Customer;
import pl.orm.TCustomerDao;

import java.util.Iterator;
import java.util.List;

/**
 * Created by wangpeng on 2016/5/8.
 */
public class TCustomerDaoImpl implements TCustomerDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public Customer findOne() {
//        return (Customer) jdbcTemplate.queryForObject("select * from t_customer limit 1", new BeanPropertyRowMapper(Customer.class));
        return (pl.orm.Customer)jdbcTemplate.queryForObject("select * from t_customer limit 1", new org.springframework.jdbc.core.BeanPropertyRowMapper(pl.orm.Customer.class));
    }

    public Customer findOne(Long id) {
        return null;
    }

    public Iterator<Customer> find() {
        return null;
    }

    public List<Customer> findAll() {
        return null;
    }

    public Customer findByCustomerId() {
        return null;
    }
}
