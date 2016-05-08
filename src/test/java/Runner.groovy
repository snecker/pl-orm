import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.PropertySource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.support.TransactionTemplate

import javax.sql.DataSource

/**
 *
 * Created by wangpeng on 2016/5/7.
 */
@ComponentScan("pl")
@EnableAutoConfiguration
@PropertySource("application.yaml")
class Runner {
    static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(Runner.class);
        DataSource ds = ac.getBean(DataSource.class);
        //jdbctemplate
        JdbcTemplate jdbcTemplate = ac.getBean(JdbcTemplate.class)

        TransactionTemplate transactionTemplate = ac.getBean(TransactionTemplate.class)

        transactionTemplate.execute{
            it.setRollbackOnly()
            println "${it}"
        }
        assert jdbcTemplate != null;
    }
}
