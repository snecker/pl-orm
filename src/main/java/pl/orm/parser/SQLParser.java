package pl.orm.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.orm.annotation.Bind;
import pl.orm.util.KEY;
import pl.orm.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
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
            logger.info("[SQLParser]Method:{}->rawSql:{}", mMethod, rawSQL);
        }
        return rawSQL;
    }

    private String parseSqlByMethod(Method method) {
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

        if (parts.length >= 2) {//column and where
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

        return "*";
    }

    private String parseWhereClauseByMethodName(Method method) {
        String methodName = method.getName();
        String[] parts = methodName.split("By");
        int parameterCnt = method.getParameterCount();

        if (parts.length == 2) {
            String[] wheres = parts[1].split("And");
            //where必须和参数列表一致
            if (wheres.length != parameterCnt) {
                throw new IllegalArgumentException(method + " 方法参数跟名字By参数不一致! ");
            }

            if (wheres.length > 1) {
                return String.join(KEY.BLANK, Arrays.stream(wheres).map(it -> {
                    String columnName = StringUtils.underscored(it);
                    return new StringBuilder("and ")
                            .append(columnName)
                            .append("=:")
                            .append(it).toString();
                }).collect(Collectors.toList()));
            } else {
                return new StringBuilder("and ")
                        .append(StringUtils.underscored(wheres[0]))
                        .append("=:")
                        .append(wheres[0]).toString();
            }
        } else if (parts.length == 1) {//没有,说明where条件由参数来拼装
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parameterCnt; i++) {
                Annotation[] oneParameterAnnos = parameterAnnotations[i];
                if (oneParameterAnnos.length == 0) {
                    throw new IllegalArgumentException("第" + (i + 1) + "个参数没有注解!");
                }
                if (oneParameterAnnos[0] instanceof Bind) {
                    String whereColumnName = ((Bind) oneParameterAnnos[0]).value();
                    String columnName = StringUtils.underscored(whereColumnName);
                    sb.append(" and ").append(columnName).append("=:").append(whereColumnName).append(KEY.BLANK);
                } else {
                    throw new IllegalArgumentException("暂时不支持其他注解:" + oneParameterAnnos[0].getClass().getName());
                }
            }
            return sb.toString();
        }
        return "";
    }

    public List<Annotation> getMethodParameterAnnotation(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        List<Annotation> retAnnotations = new ArrayList<>();
        for (int i = 0; i < method.getParameterCount(); i++) {
            Annotation[] oneParameterAnnos = parameterAnnotations[i];
            if (oneParameterAnnos.length == 0) {
                throw new IllegalArgumentException("第" + (i + 1) + "个参数没有注解!");
            }
            retAnnotations.add(oneParameterAnnos[0]);
        }
        long distinctSize = retAnnotations.stream().map(it -> it.annotationType()).distinct().count();
        if (distinctSize > 1) {
            throw new IllegalArgumentException("一个方法只允许出现一种Bind注解!");
        }
        return retAnnotations;
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
