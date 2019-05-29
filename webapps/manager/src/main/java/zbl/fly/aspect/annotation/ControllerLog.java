package zbl.fly.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述:
 *
 * @author: 张彬雷 [zhangbinlei@xinnet.com]
 * @since: 2019/5/29
 * @update:[变更日期YYYY-MM-DD][更改人姓名][变更描述]
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ControllerLog {
    String value() default "";
}
