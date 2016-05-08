package pl.orm.autoconfigure;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import pl.orm.annotation.Dao;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Created by wangpeng on 2016/5/7.
 */

@Configuration
public class PLOrmConfigure implements InitializingBean {
    //用于手动添加对象
    @Autowired
    ConfigurableListableBeanFactory beanFactory;

    private static String scanPackage = "pl";


    @PostConstruct
    public void init() {
        //扫描包下的类
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        CustomizedClassPathScanningCandidateComponentProvider provider = new CustomizedClassPathScanningCandidateComponentProvider(Dao.class);
        Set<BeanDefinition> beanDefinitions = provider.findCandidateComponents(scanPackage);
        for (BeanDefinition beanDef : beanDefinitions) {
            System.out.println("==============>" + beanDef);
            //通过字节码加强工具生成一个实现类
            assistBean(beanDef);
        }
    }

    private void assistBean(BeanDefinition beanDef) {

    }
}

/**
 * 使用这种方式,缺陷是会执行一次spring自定义的操作
 * 好处是不用引用其他代码了
 */
class CustomizedClassPathScanningCandidateComponentProvider extends ClassPathScanningCandidateComponentProvider {

    public CustomizedClassPathScanningCandidateComponentProvider(Class<? extends Annotation> customizeAnnotation) {
        super(false);
        this.addIncludeFilter(new AnnotationTypeFilter(customizeAnnotation, false, true));
    }


    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        /**
         * 父类是排除了interface的,所以覆盖并把限制去掉
         */
        return beanDefinition.getMetadata().isIndependent();
    }
}