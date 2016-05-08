package pl.orm.annotation;

import java.lang.annotation.*;

/**
 * Created by wangpeng on 2016/5/7.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dao {
    String value() default "";
}
