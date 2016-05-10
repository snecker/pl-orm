import javassist.*
import javassist.bytecode.AnnotationsAttribute
import javassist.bytecode.ClassFile
import javassist.bytecode.ConstPool
import javassist.bytecode.annotation.Annotation
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.jdbc.core.JdbcOperations
import pl.TCustomerDaoImpl
import pl.orm.Customer
import pl.orm.parser.SQLParser
import pl.orm.TCustomerDao

import java.lang.reflect.Method
import java.lang.reflect.Type

/**
 * Created by wangpeng on 2016/5/8.
 */
class TestAssist extends BaseTest {
    @Autowired
    ConfigurableListableBeanFactory beanFactory;

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void name() throws Exception {
        //debug
        CtClass.debugDump = "./dump";
        //        /*获得运行时类的上下文*/
        ClassPool pool = ClassPool.getDefault();
        //
        Class sourceClass = TCustomerDao.class;

        CtClass newClass = pool.makeClass("${sourceClass.getName()}\$EnhancerImpl")


        //原来的接口定义
        newClass.addInterface(pool.get(sourceClass.getName()))
        //jdbcOperation定义,方便原生操作.
        newClass.addInterface(pool.get(JdbcOperations.class.getName()))

        ///////////////// 添加field

        CtField jdbcTemplateCtField = CtField.make('''
private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;
''', newClass)

        ClassFile ccFile = newClass.getClassFile();
        ConstPool constPool = ccFile.getConstPool();
        AnnotationsAttribute attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation autowiredAnno = new Annotation("org.springframework.beans.factory.annotation.Autowired", constPool);
        attribute.addAnnotation(autowiredAnno);
        //
        jdbcTemplateCtField.getFieldInfo().addAttribute(attribute)
        //
        newClass.addField(jdbcTemplateCtField);

        ////////////////// 添加覆盖方法.

        //本身
        Method[] selfMethods = sourceClass.getDeclaredMethods();
        for (Method m : selfMethods) {
            String methodName = m.getName();
            Class clazz = m.getReturnType();
            Type type = m.getGenericReturnType();
            //实现方法体
//            CtMethod ctMethod = CtMethod.make("public ${type} ${methodName}", proxyClass)
            CtMethod sourceCtMethod = pool.getMethod(sourceClass.getName(), methodName)
            //copy
            CtMethod newCtMethod = CtNewMethod.copy(sourceCtMethod, methodName, newClass, null);
            //TODO  判断参数列表

//            System.out.println("\$class=>"+\$class);
//            System.out.println(\$\$);
//            System.out.println(\$type);
//            System.out.println(\$0);
//            System.out.println(\$sig);
//            System.out.println("aaaaaaa=${type.getTypeName()}");
            //parse sql
            SQLParser sqlParser = new SQLParser(m);
            String sql = sqlParser.getRawSql();
            //
            int parameterCount = m.getParameterCount();
            StringBuilder sb = new StringBuilder("")
            if (parameterCount > 0) {
                sb.append(",new Object[]{")
                (1..parameterCount).each {
                    if (it > 1) {
                        sb.append(",")
                    }
                    sb.append("\$${it}")
                }
                sb.append("}")
            }

            String params = sb.toString()

            newCtMethod.setBody("""{

java.util.List list = jdbcTemplate.query("${sql}"${params},new org.springframework.jdbc.core.BeanPropertyRowMapper(\$type));
return list ==null || list.size()==0 ? null :(${clazz.getName()})list.get(0);
}""".toString())
            newClass.addMethod(newCtMethod)
        }
        //

//        //超类
//        Method[] jdbcOperationMethods = JdbcOperations.class.getDeclaredMethods();
//
//        //operation的
//        for (Method m : jdbcOperationMethods) {
//            CtMethod ctMethod = pool.getMethod(JdbcOperations.class.getName(), m.getName())
//            ctMethod.setBody("return null;")
//            proxyClass.addMethod(ctMethod)
//        }

        newClass.setModifiers(newClass.getModifiers() & ~Modifier.ABSTRACT);

        Class<TCustomerDao> testClz = newClass.toClass()

        TCustomerDao wrappedClassInstance = testClz.newInstance()

        //inject all field that annotated with @Autowired
        autowiredAnnotationBeanPostProcessor.processInjection(wrappedClassInstance)

        beanFactory.registerSingleton(sourceClass.getTypeName(), wrappedClassInstance)

        //test
        TCustomerDao inContainerInstance = applicationContext.getBean(TCustomerDao.class)


        Customer ret = inContainerInstance.selectByCustomerIdAndAgentCode(17, "testwanglu");
        println "结果===${ret !=null ?ret.getTitle() :'结果是空!'}"
    }

    //手动添加没有@component的到 容器里面

    @Autowired
    AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor;

    @Test
    public void testManualAddClass() throws Exception {
        TCustomerDaoImpl tCustomerDao = new TCustomerDaoImpl();

        autowiredAnnotationBeanPostProcessor.processInjection(tCustomerDao)


        beanFactory.registerSingleton("tCustomerDaoImpl", tCustomerDao)


        TCustomerDao dao = applicationContext.getBean(TCustomerDaoImpl.class)

        Customer cus = dao.findOne();
        //
        println "id========>${cus.getCustomerId()}"
    }


}
