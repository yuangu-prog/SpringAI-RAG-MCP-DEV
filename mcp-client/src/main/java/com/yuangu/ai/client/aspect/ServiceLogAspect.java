package com.yuangu.ai.client.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * ServiceLogAspect
 *
 * @author ckliu
 * @since 2026-01-15 21:35:50
 */

@Slf4j
@Aspect
@Component
public class ServiceLogAspect {

    @Around("execution(* com.yuangu.ai.client..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        StopWatch watch = new StopWatch();

        watch.start("任务1");

        String s = pjp.getTarget().getClass().getName() + "." + pjp.getSignature().getName();

        Object result = pjp.proceed();

        watch.stop();

        watch.start("任务2");
        watch.stop();

        watch.start("任务3");
        watch.stop();

        // 总结
        watch.prettyPrint();
        watch.shortSummary();
        return result;
    }
}
