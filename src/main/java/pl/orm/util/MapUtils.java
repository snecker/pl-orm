package pl.orm.util;

import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangpeng on 2016/5/10.
 */
public class MapUtils {
    public static Map<String, Object> toMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        BeanUtils.copyProperties(obj, map);
        return map;
    }

    public static Map<String, Object> mergeListToMap(List<String> names, List<Object> values) {
        if (names.size() != values.size()) {
            throw new IllegalArgumentException("两个list大小不一致!");
        }

        HashMap map = new HashMap(names.size());
        for (int i = 0; i < names.size(); i++) {
            map.put(names.get(i), values.get(i));
        }
        return map;
    }
}
