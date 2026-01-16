package com.yuangu.ai.client.aspect;

import groovy.util.logging.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class ServiceLogAspect {

    @Around("execution(* com.yuangu.ai.client.controller..*.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        StopWatch watch = new StopWatch();

        watch.start("任务1");
        watch.stop();

        watch.start("任务2");
        watch.stop();

        watch.start("任务3");
        watch.stop();
        Object result = point.proceed();

        watch.prettyPrint();
        watch.shortSummary();

        // 总任务耗时
        long totalTimeMillis = watch.getTotalTimeMillis();
        int taskCount = watch.getTaskCount();
        return result;
    }
}
