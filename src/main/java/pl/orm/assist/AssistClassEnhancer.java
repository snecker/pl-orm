package pl.orm.assist;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import pl.orm.annotation.Bind;
import pl.orm.annotation.BindBean;
import pl.orm.annotation.BindMap;
import pl.orm.parser.SQLParser2;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by wangpeng on 2016/5/9.
 */
public class AssistClassEnhancer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Class sourceClass;
    private final ClassPool classPool;
    private final CtClass proxyCtClass;

    private static final String ASSIST_CLASS_SUFFIX = "$PlOrmEnhancer";

    static {
        CtClass.debugDump = "./dump";
    }

    public AssistClassEnhancer(Class sourceClass) {
        this.sourceClass = sourceClass;
        this.classPool = ClassPool.getDefault();

        this.proxyCtClass = classPool.makeClass(sourceClass.getName() + ASSIST_CLASS_SUFFIX);

    }

    /**
     * 字节码增强
     * 实现接口
     */
    public Class assist() {
        try {
            this.proxyCtClass.setInterfaces(
                    classPool.get(new String[]{sourceClass.getName(), JdbcOperations.class.getName()})
            );
            //annotation
            CtField jdbcTemplateCtField = CtField.make(String.format(
                    "%s namedParameterJdbcTemplate;", NamedParameterJdbcTemplate.class.getName()
            ), proxyCtClass);
            this.assistAnnotation(jdbcTemplateCtField, Autowired.class);
            //implement methods
            CtClass sourceCtClass = classPool.get(sourceClass.getName());
            //
            this.assistMethods(sourceCtClass.getDeclaredMethods());
//            proxyCtClass.writeFile();
            return proxyCtClass.toClass();

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void assistMethods(CtMethod[] declaredCtMethods) throws NotFoundException, CannotCompileException, ClassNotFoundException {
        for (CtMethod srcCtMethod : declaredCtMethods) {
            CtMethod proxyCtMethod = CtNewMethod.copy(srcCtMethod, proxyCtClass, null);
            String sql = SQLParser2.parseSQL(srcCtMethod);
            //
            int parameterCount = srcCtMethod.getParameterTypes().length;
            String methodBody = "";
            if (parameterCount > 0) {
                List<java.lang.annotation.Annotation> annotationList = SQLParser2.getMethodParameterAnnotation(srcCtMethod);
                java.lang.annotation.Annotation firstAnno = annotationList.get(0);
                if (firstAnno instanceof Bind) {
                    String nameArrayString = String.join(",", annotationList.stream().map(it -> "\"" + ((Bind) it).value() + "\"").collect(Collectors.toList()));
                    String valueArrayString = String.join(",", IntStream.range(1, parameterCount + 1)
                            .mapToObj(it -> new StringBuilder("$").append(it).toString())
                            .collect(Collectors.toList()));
                    methodBody = String.format("{" +
                            "java.util.List nameList=java.util.Arrays.asList(new String []{%s});" +
                            "java.util.List valueList=java.util.Arrays.asList(new Object []{%s});" +
                            "java.util.Map  map =pl.orm.util.MapUtils.mergeListToMap(nameList,valueList);" +
                            "java.util.List list =namedParameterJdbcTemplate.query(\"%s\",map,new org.springframework.jdbc.core.BeanPropertyRowMapper($type));" +
                            "return list == null || list.size() == 0 ? null:(%s)list.get(0);" +
                            "}", nameArrayString, valueArrayString, sql, srcCtMethod.getReturnType().getName());
                } else if (firstAnno instanceof BindMap) {
                    methodBody = String.format("{" +
                            "String sqlWhere=pl.orm.util.MapUtils.mapToSqlWhereClause($1);" +
                            "java.util.List list = " +
                            "namedParameterJdbcTemplate.query(\"%s\"+sqlWhere,$1,new org.springframework.jdbc.core.BeanPropertyRowMapper($type));" +
                            "return list == null || list.size() == 0 ? null:(%s)list.get(0);" +
                            "}", sql, srcCtMethod.getReturnType().getName());
                } else if (firstAnno instanceof BindBean) {
                    methodBody = String.format("{" +
                            "String sqlWhere=pl.orm.util.BeanUtil.convertNotNullWhereClause($1);" +
                            "java.util.List list = " +
                            "namedParameterJdbcTemplate.query(\"%s\"+sqlWhere,new org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource($1)," +
                            "new org.springframework.jdbc.core.BeanPropertyRowMapper($type));" +
                            "return list == null || list.size() == 0 ? null:(%s)list.get(0);" +
                            "}", sql, srcCtMethod.getReturnType().getName());
                }
            }


            logger.info("[Method Body Print] {}", methodBody);
            proxyCtMethod.setBody(methodBody);
            proxyCtClass.addMethod(proxyCtMethod);
        }

        //abstract to concrete;
        proxyCtClass.setModifiers(proxyCtClass.getModifiers() & ~Modifier.ABSTRACT);
    }

    /**
     * 参考 http://lzxz1234.github.io/java/2014/07/25/Get-Method-Parameter-Names-With-Javassist
     *
     * @param method
     * @return
     * @throws NotFoundException
     */
    public String[] parseMethodParameterNames(Method method) throws NotFoundException {
        CtClass sourceCtClass = classPool.get(method.getDeclaringClass().getName());
        int methodParameterLength = method.getParameterCount();
        //
        Class[] methodParameterTypes = method.getParameterTypes();
        CtClass[] parameterCtClazz = new CtClass[methodParameterLength];
        for (int i = 0; i < methodParameterLength; i++) {
            parameterCtClazz[i] = classPool.getCtClass(methodParameterTypes[i].getName());
        }

//        CtMethod sourceCtMethod = classPool.getMethod(sourceClass.getName(), method.getName());
        CtMethod sourceCtMethod = sourceCtClass.getDeclaredMethod(method.getName(), parameterCtClazz);

        MethodInfo methodInfo = sourceCtMethod.getMethodInfo2();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        String[] parameterNames = new String[methodParameterLength];

        TreeMap<Integer, String> sortMap = new TreeMap<Integer, String>();
        for (int i = 0; i < attr.tableLength(); i++)
            sortMap.put(attr.index(i), attr.variableName(i));
        int pos = Modifier.isStatic(sourceCtMethod.getModifiers()) ? 0 : 1;
        parameterNames = Arrays.copyOfRange(sortMap.values().toArray(new String[0]), pos, parameterNames.length + pos);

        return parameterNames;
    }

    public void assistAnnotation(CtField ctField, Class<? extends java.lang.annotation.Annotation> annoClass) {
        ClassFile ccFile = proxyCtClass.getClassFile();
        ConstPool constPool = ccFile.getConstPool();
        AnnotationsAttribute attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation autowiredAnno = new Annotation(annoClass.getName(), constPool);
        attribute.addAnnotation(autowiredAnno);
        //
        ctField.getFieldInfo().addAttribute(attribute);
        //
        try {
            proxyCtClass.addField(ctField);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

}
