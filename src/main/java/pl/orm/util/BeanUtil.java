package pl.orm.util;

import org.springframework.beans.BeanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangpeng on 2016/5/11.
 */
public class BeanUtil {
    public static String convertNotNullWhereClause(Object source) {
        Map map = new ConcurrentHashMap<>();
        BeanUtils.copyProperties(source, map);
        map.forEach((key, value) -> {
            if (value == null) {
                map.remove(key);
            }
        });
        return MapUtils.mapToSqlWhereClause(map);
    }
}
