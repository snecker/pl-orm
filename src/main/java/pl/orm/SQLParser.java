package pl.orm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.orm.util.KEY;
import pl.orm.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static pl.orm.util.StringUtils.underscored;

/**
 * Created by wangpeng on 2016/5/9.
 */
public class SQLParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Method mMethod;
    private final Annotation[] annotations;

    private static final String[] CURDS = new String[]{"select", "update", "insert", "delete"};

    private String rawSQL;

    public SQLParser(Method mMethod) {
        this.mMethod = mMethod;
        this.annotations = mMethod.getDeclaredAnnotations();
    }

    public String getRawSql() {
        if (annotations.length == 0) {
            rawSQL = parseSqlByMethod(mMethod);
            System.out.println("rawSql:{}" + rawSQL);
        }
        return rawSQL;
    }

    private String parseSqlByMethod(Method mMethod) {
        String sql = parseSqlByMethodName(mMethod);

        return sql;
    }

    private String parseSqlByMethodName(Method method) {
        String methodName = method.getName();
        if (!validateMethodName(methodName)) {
            throw new IllegalArgumentException(methodName + " is not support,now support " + Arrays.toString(CURDS));
        }
        //
        String clazzName = method.getDeclaringClass().getSimpleName();
        String tableName = getTableNameByClazzName(clazzName);
        String opt = getOperationName(methodName);
        String whereClause = parseWhereClauseByMethodName(method);
        StringBuilder sb = new StringBuilder();

        //
        if (KEY.CURD.SELECT.toString().equals(opt)) {
            String selectColumns = parseColumnByMethodName(opt, methodName);
            String assembleSql = String.join(KEY.BLANK, Arrays.asList(
                    opt,
                    selectColumns,
                    "from",
                    tableName,
                    "where",
                    "1=1",
                    whereClause
            ));
            sb.append(assembleSql);

        } else if (KEY.CURD.UPDATE.toString().equals(opt)) {

        }
        return sb.toString();
    }

    private String getTableNameByClazzName(String clazzName) {
        String tableName = clazzName.replaceAll("Dao$", "");
        tableName = underscored(tableName);
        return tableName;
    }

    private String parseColumnByMethodName(String opt, String methodName) {
        String[] parts = methodName.split(opt + "|By");

        if (parts.length == 3) {//column and where
            String column = parts[1];

            if (StringUtils.isBlank(column)) {
                return "*";
            }
            //
            String[] columns = stream(column.split("And")).map(it -> underscored(it))
                    .collect(Collectors.toList()).toArray(new String[0]);

            return StringUtils.join(columns, ",");
        } else if (parts.length == 2) {
        } else if (parts.length == 1) {

        }

        return "";
    }

    private String parseWhereClauseByMethodName(Method method) {
        String methodName = method.getName();
        String[] parts = methodName.split("By");
        if (parts.length == 2) {
            String[] wheres = parts[1].split("And");
            //where必须和参数列表一致
            int parameterCnt = method.getParameterCount();
            if (wheres.length != parameterCnt) {
                throw new IllegalArgumentException(method + " 方法参数跟名字By参数不一致! ");
            }

            if (wheres.length > 1) {
                return String.join(KEY.BLANK, Arrays.stream(wheres).map(it -> {
                    String lower = StringUtils.underscored(it);
                    return new StringBuilder("and ")
                            .append(lower)
                            .append("=? ").toString();
                }).collect(Collectors.toList()));
            } else {
                return new StringBuilder("and ")
                        .append(StringUtils.underscored(wheres[0]))
                        .append("=? ").toString();
            }

        }
        return "";
    }

    private boolean validateMethodName(String methodName) {
        if (methodName == null) {
            throw new NullPointerException("methodName cannot be null");
        }
        for (int i = 0; i < CURDS.length; i++) {
            if (methodName.startsWith(CURDS[i])) {
                return true;
            }
        }

        return false;
    }

    private String getOperationName(String methodName) {
        for (int i = 0; i < CURDS.length; i++) {
            if (methodName.startsWith(CURDS[i])) {
                return CURDS[i];
            }
        }
        throw new IllegalArgumentException("暂时不支持这种命名:" + methodName);
    }
}
