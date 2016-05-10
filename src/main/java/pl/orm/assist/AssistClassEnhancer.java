package pl.orm.assist;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.orm.parser.SQLParser;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by wangpeng on 2016/5/9.
 */
public class AssistClassEnhancer {
    private final Class sourceClass;
    private final ClassPool classPool;
    private final CtClass proxyClass;

    private static final String ASSIST_CLASS_SUFFIX = "$PlOrmEnhancer";

    public AssistClassEnhancer(Class sourceClass) {
        this.sourceClass = sourceClass;
        this.classPool = ClassPool.getDefault();
        this.proxyClass = classPool.makeClass(sourceClass.getName() + ASSIST_CLASS_SUFFIX);
    }

    /**
     * 字节码增强
     * 实现接口
     */
    public Class assist() {
        try {
            this.proxyClass.setInterfaces(
                    classPool.get(new String[]{sourceClass.getName(), JdbcOperations.class.getName()})
            );
            //set import
//            classPool.importPackage("org.springframework.jdbc.core.*");
            //annotation
            CtField jdbcTemplateCtField = CtField.make(String.format(
                    "private %s jdbcTemplate;", JdbcTemplate.class.getName()
            ), proxyClass);
            this.assistAnnotation(jdbcTemplateCtField, Autowired.class);
            //implement methods
            this.assistMethods(sourceClass.getDeclaredMethods());

            //inject to spring container
//            this.autowiredAnnotationBeanPostProcessor.processInjection(proxyClass.toClass().newInstance());
            return proxyClass.toClass();

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void assistMethods(Method[] declaredMethods) throws NotFoundException, CannotCompileException {
        for (Method m : declaredMethods) {
            String methodName = m.getName();
            CtMethod sourceCtMethod = classPool.getMethod(sourceClass.getName(), methodName);
            CtMethod proxyCtMethod = CtNewMethod.copy(sourceCtMethod, methodName, proxyClass, null);
            String sql = new SQLParser(m).getRawSql();
            //
            int parameterCount = m.getParameterCount();
            StringBuilder sb = new StringBuilder("");
            if (parameterCount > 0) {
                sb.append(",new Object[]{");
                for (int idx = 1; idx <= parameterCount; idx++) {
                    if (idx > 1) {
                        sb.append(",");
                    }
                    sb.append('$').append(idx);
                }
                sb.append("}");
            }

            String params = sb.toString();

            proxyCtMethod.setBody(
                    String.format("{" +
                            "%s list=jdbcTemplate.query(\"%s\" %s,new %s($type));\n" +
                            "return list == null || list.size() == 0 ? null:(%s)list.get(0);" +
                            "}", List.class.getName(), sql, params, BeanPropertyRowMapper.class.getName(), m.getReturnType().getName()));
            proxyClass.addMethod(proxyCtMethod);
        }

        //abstract to concrete;
        proxyClass.setModifiers(proxyClass.getModifiers() & ~Modifier.ABSTRACT);
    }

    public void assistAnnotation(CtField ctField, Class<? extends java.lang.annotation.Annotation> annoClass) {
        ClassFile ccFile = proxyClass.getClassFile();
        ConstPool constPool = ccFile.getConstPool();
        AnnotationsAttribute attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation autowiredAnno = new Annotation(annoClass.getName(), constPool);
        attribute.addAnnotation(autowiredAnno);
        //
        ctField.getFieldInfo().addAttribute(attribute);
        //
        try {
            proxyClass.addField(ctField);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

}
