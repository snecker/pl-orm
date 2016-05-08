# pl-orm
java lightweight orm framework

# todo
- 只要一个注解就能操作数据库(可不写sql)
```java
@Dao
public interface TCustomerDao{
    Customer findByCustomerId(Long id);
    List<Customer> findAll();
}
```
- 基于springboot
- 动态注册interface到spring容器

流程

一个 interface tableDao
通过javassist工具动态生成 tableDao的实现类TableDaoImpl,并注册到spring容器中
通过@autowire就可以使用

# 说明

## 单表
根据参数名字拼sql

Customer select(String agentCode,Long csId);
Customer select(Customer cus);
List<Customer> select(String agentCode,Long csId);

## 自定义查询
@Select("select * from t_customer t1 join t_emp_cus_relation t2 on t2.id = t1.id where t1.agent_code=#agentCode#")
Customer select(String agentCode,Long csId);