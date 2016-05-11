import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import org.junit.Test;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import pl.orm.Customer;
import pl.orm.TCustomerDao;
import pl.orm.assist.AssistClassEnhancer;
import pl.orm.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangpeng on 2016/5/7.
 */
public class TestReflection {

    public List<String> getList(String name, List<String> list, int val1, Integer val2) {
        return Arrays.asList("1", "2");
    }

    @Test
    public void test() throws Exception {

        Class<TestReflection> cc = TestReflection.class;

        Method[] methos = cc.getDeclaredMethods();

        for (Method m : methos) {
            System.out.println("" + m.getName());
        }
    }

    @Test
    public void testUncapti() throws Exception {
        String aa = "TCustomerDao";
        String ret = StringUtils.underscored(aa);
        System.out.println("ret=" + ret);
    }

    @Test
    public void testGroup() throws Exception {

        String str = "selectAgentCodeAndCsCustomerIdByCustomerIdAndAgentCode";

        String[] parts = str.split("select" + "|By");

        System.out.println(parts);
    }

    @Test
    public void testAssist() throws Exception {
        AssistClassEnhancer assistClassEnhancer = new AssistClassEnhancer(TCustomerDao.class);
        Class newClass = assistClassEnhancer.assist();
        ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        Method[] methods = newClass.getDeclaredMethods();
        for (Method m : methods) {
//            String[] strings = assistClassEnhancer.parseMethodParameterNames(m);

            String[] strings = parameterNameDiscoverer.getParameterNames(m);
            System.out.println(Arrays.toString(strings));
        }
    }

    @Test
    public void name() throws Exception {
        CtMethod m = ClassPool.getDefault().getCtClass(TCustomerDao.class.getName()).getDeclaredMethod("selectByCustomerIdAndAgentCode");
        Map<Integer, String> hashNameParam = new HashMap<Integer, String>();
        CodeAttribute codeAttribute = (CodeAttribute) m.getMethodInfo().getAttribute("Code");
        if (codeAttribute != null) {
            LocalVariableAttribute localVariableAttribute = (LocalVariableAttribute) codeAttribute.getAttribute("LocalVariableTable");
            if (localVariableAttribute != null && localVariableAttribute.tableLength() >= m.getParameterTypes().length) {
                for (int i = 0; i < m.getParameterTypes().length + 1; i++) {
                    String name = localVariableAttribute.getConstPool().getUtf8Info(localVariableAttribute.nameIndex(i));
                    if (!name.equals("this")) {
                        hashNameParam.put(i, name);
                    }
                }
            }
        }

        System.out.println("code");
    }

//    @Autowired


    @Test
    public void testNameing() throws Exception {
        Class clazz = ClzB.class;
        ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
//            String[] strings = assistClassEnhancer.parseMethodParameterNames(m);
            String[] strings = parameterNameDiscoverer.getParameterNames(m);
            System.out.println(Arrays.toString(strings));
        }
    }

    @Test
    public void testMapping() throws Exception {
        Class clzb = ClzA.class;
        Method[] ms = clzb.getDeclaredMethods();
        for (Method m : ms) {
            Parameter[] ps = m.getParameters();
            for (Parameter p : ps) {
                System.out.println("ps ->" + p.getName());
            }
        }
    }

    @Test
    public void testSplit() throws Exception {
        Class cc = TCustomerDao.class;

        Method[] ms = cc.getDeclaredMethods();
        for (Method d : ms) {
            AnnotatedType[] ts = d.getAnnotatedParameterTypes();
            Annotation[][] anno = d.getParameterAnnotations();

            System.out.println(ts);
        }
    }

    @Test
    public void testMap() throws Exception {
        java.util.List<String> nameList = java.util.Arrays.asList("customerId", "agentCode");
        java.util.List<Object> valueList = java.util.Arrays.asList(14, "testwagnlu");
        java.util.Map map = pl.orm.util.MapUtils.mergeListToMap(nameList, valueList);

        System.out.println("map--");
    }

    @Test
    public void testDupmethod() throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        CtClass destCtClass = classPool.makeClass(TCustomerDao.class.getName() + "$NewClassName");
        CtClass srcCtClass = classPool.get(TCustomerDao.class.getName());
        destCtClass.setInterfaces(classPool.get(new String[]{TCustomerDao.class.getName()}));

        CtMethod[] srcMethods = srcCtClass.getDeclaredMethods();
        for (CtMethod srcMethod : srcMethods) {
            CtMethod newMethod = CtNewMethod.copy(srcMethod, destCtClass, null);
            newMethod.setModifiers(javassist.Modifier.PUBLIC);
            newMethod.setBody("{return null;}");
            destCtClass.addMethod(newMethod);
        }

//        CtClass returnType = classPool.get(Customer.class.getName());
//        CtClass[] paramTypes = classPool.get(new String[]{Long.class.getName(), String.class.getName()});
//        destCtClass.addMethod(CtNewMethod.make(javassist.Modifier.PUBLIC, returnType, "selectCustomerIdAndContactsBy", paramTypes, null, "{return null;}", destCtClass));


        Class clz = destCtClass.toClass();
        System.out.println(destCtClass.toClass());
    }

    @Test
    public void testNull() throws Exception {
        Customer param = new Customer();
        param.setCustomerId(23);
        param.setAgentCode("testwanglu");
        param.setId(1234L);


        Field[] fields = param.getClass().getFields();

        for (Field f : fields) {
            f.setAccessible(true);
            System.out.println("f->" + f.getName() + ":" + f.get(param));
        }

    }
}

class ClzA {
    public String getValue(String csId, String cusId) {
        return null;
    }
}

abstract class ClzB {
    //可以获取参数
    public String getValue(String csId, String cusId) {
        return null;
    }

    //可以获取参数
    public abstract String getValue(String csId, String cusId, String cc);
}
