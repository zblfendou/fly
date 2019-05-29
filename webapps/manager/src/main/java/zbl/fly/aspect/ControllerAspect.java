package zbl.fly.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import zbl.fly.aspect.annotation.ControllerLog;

import javax.inject.Named;

/**
 * 描述:控制层切片
 *
 * @author: 张彬雷 [zhangbinlei@xinnet.com]
 * @since: 2019/5/29
 * @update:[变更日期YYYY-MM-DD][更改人姓名][变更描述]
 */
@Aspect
@Slf4j
@Named
public class ControllerAspect {

    @Pointcut("@annotation(zbl.fly.aspect.annotation.ControllerLog)")
    public void cutMethod() {
    }

    @Around("cutMethod()&&@annotation(controllerLog)")
    public void beforeMethod(JoinPoint jp, ControllerLog controllerLog) {
        log.debug(String.format(controllerLog.value(), jp.getArgs()));
    }
}
