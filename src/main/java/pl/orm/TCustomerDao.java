package pl.orm;

import pl.orm.annotation.Bind;
import pl.orm.annotation.BindBean;
import pl.orm.annotation.BindMap;
import pl.orm.annotation.Dao;

import java.util.Map;

/**
 * Created by wangpeng on 2016/5/7.
 */
@Dao
public interface TCustomerDao {
//    Customer selectByCustomerIdAndAgentCode(Long customerId, String agentCode);

    Customer selectBy(@Bind("customerId") Long customerId,
                      @Bind("agentCode") String agentCode);

    Customer selectCustomerIdAndContactsBy(@Bind("customerId") Long customerId,
                                           @Bind("agentCode") String agentCode);

    Customer selectCustomerIdAndContactsBy(@BindMap Map query);

    Customer selectCustomerIdAndContactsBy(@BindBean Customer query);

//    Customer selectTitleAndStatusAndMobileByCustomerIdAndAgentCode(Long customerId, String agentCode);

//    public Iterator<Customer> find();
//
//    public List<Customer> findAll();
//
//    public Customer findByCustomerId();
}
