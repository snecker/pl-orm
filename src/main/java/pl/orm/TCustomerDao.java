package pl.orm;

import pl.orm.annotation.Dao;

/**
 * Created by wangpeng on 2016/5/7.
 */
@Dao
public interface TCustomerDao {
    Customer selectByCustomerIdAndAgentCode(Long customerId, String agentCode);

    Customer selectTitleAndStatusAndMobileByCustomerIdAndAgentCode(Long customerId, String agentCode);

//    public Iterator<Customer> find();
//
//    public List<Customer> findAll();
//
//    public Customer findByCustomerId();
}
