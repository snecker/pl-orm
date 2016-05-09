# pl-orm
java lightweight orm framework based on Spring-JDBC

self study

# todo
- 查:select
- 改:update
- 增:insert
- 删:delete

# 说明

## 单表
根据参数名字拼sql

Customer select(String agentCode,Long csId);
Customer select(Customer cus);
List<Customer> select(String agentCode,Long csId);

## 自定义查询
@Select("select * from t_customer t1 join t_emp_cus_relation t2 on t2.id = t1.id where t1.agent_code=#agentCode#")
Customer select(String agentCode,Long csId);