package pl.orm.handle;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wangpeng on 2016/5/9.
 */
public class BeanPropertyRowCallBackHandler<T> extends BeanPropertyRowMapper<T> implements ResultSetExtractor<T> {
    public BeanPropertyRowCallBackHandler(Class<T> mappedClass) {
        super(mappedClass);
    }

    @Override
    public T extractData(ResultSet rs) throws SQLException, DataAccessException {
        return this.mapRow(rs, 0);
    }
}
