# pl-orm
java lightweight orm framework based on Spring-JDBC

self study

# todo
- 查:select
- 改:update
- 增:insert
- 删:delete

改成基于 NamedParameterJdbcTemplate
支持selectBy(Object)

# 说明

## 目前支持
根据参数名字拼sql

- Customer selectBy(String agentCode,Long csId);
- Customer selectBy(Object cus);
- Customer selectByAgentCodeAndCsId(String agentCode,Long csId);
- Customer selectByAgentCodeAndCsId(Object obj);


List<Customer> select(String agentCode,Long csId);

## 自定义查询
@Select("select * from t_customer t1 join t_emp_cus_relation t2 on t2.id = t1.id where t1.agent_code=?")
Customer select(String agentCode,Long csId);