package pl.orm;

/**
 * Created by wangpeng on 2016/5/7.
 */
//@Dao
public interface TCustomerDao {
    Customer findOne(Long id);

//    public Iterator<Customer> find();
//
//    public List<Customer> findAll();
//
//    public Customer findByCustomerId();
}
