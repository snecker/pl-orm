package pl;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * 注册@Dao的类到spring容器中
 * Created by wangpeng on 2016/5/7.
 */
//@Component
public class DaoProcessor implements BeanPostProcessor/*, BeanFactoryPostProcessor*/ {

    @Autowired
    ConfigurableListableBeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        Class clz = bean.getClass();
//        if (clz.equals(TCustomerDao.class)) {
//            System.out.println("true========");
//        }
//        System.out.println("---->beanPost:" + bean);
        return bean;
    }

//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        System.out.println("------------->postProcessBeanFactory");
////        beanFactory.registerSingleton();
//    }
}
