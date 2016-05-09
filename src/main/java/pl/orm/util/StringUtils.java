package pl.orm.util;

import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangpeng on 2016/5/9.
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    /*将hello_world转换为helloWorld*/
    public static String camelize(String v) {
        return transform(v, (c, i) -> {
            String sc = String.valueOf(c);
            if (c.equals('_') || c.equals('-')) return "";
            if (i == 0) return sc;

            Character lc = Character.valueOf(v.charAt(i - 1));
            return (lc.equals('_') || lc.equals('-')) ? sc.toUpperCase() : sc;
        });
    }

    /*将HelloWorld转换为hello_world*/
    public static String underscored(String v) {
        return transform(v, (c, i) -> {
            String sc = String.valueOf(c);

            if (Character.isUpperCase(c)) {
                sc = sc.toLowerCase();
                if (i != 0) sc = "_" + sc;
            }
            return sc;
        });
    }

    /*转换字符串*/
    public static String transform(String v, BiFunction<Character, Integer, String> charMapper) {
        if (v == null || v.length() == 0) return v;
        StringBuilder builder = new StringBuilder();
        for (int i = 0, len = v.length(); i < len; i++) {
            builder.append(charMapper.apply(v.charAt(i), i));
        }
        return builder.toString();
    }

    public static String find(String v, String regex, int group) {
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(v);
        int groupCnt = m.groupCount();
        if (group > groupCnt) {
            throw new IllegalArgumentException(String.format("%s > %s is illegal", group, groupCnt));
        }
        if(m.find()){
            return m.group(group);
        }
        return null;
    }
}
