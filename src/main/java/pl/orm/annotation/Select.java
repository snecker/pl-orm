package pl.orm.annotation;

import java.lang.annotation.*;

/**
 * Created by wangpeng on 2016/5/9.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Select {
    String value() default "";
}
