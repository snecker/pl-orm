package pl.orm.annotation;

import java.lang.annotation.*;

/**
 * Created by wangpeng on 2016/5/10.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bind {
    String value() default "";
}
