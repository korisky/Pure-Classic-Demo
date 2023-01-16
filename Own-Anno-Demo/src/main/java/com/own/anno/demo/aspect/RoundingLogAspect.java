package com.own.anno.demo.aspect;

import com.own.anno.demo.annotation.RoundingLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Aspect for controller
 *
 * @author Roylic
 * 2022/11/24
 */
@Slf4j
@Aspect
@Component
public class RoundingLogAspect {

    @Pointcut("execution(* (@com.own.anno.demo.annotation.RoundingLog *..*).*(..))")
    public void roundingLogPointcut() {
    }


    /**
     * Only logging, exceptions would let Global Exception Handler handle
     */
    @Around("roundingLogPointcut()")
    public Object roundingLogExecution(ProceedingJoinPoint pjp) throws Throwable {

        Class<?> aClass = pjp.getTarget().getClass();
        RoundingLog annotation = AnnotationUtils.findAnnotation(pjp.getTarget().getClass(), RoundingLog.class);
        annotation = (null == annotation) ? AnnotationUtils.findAnnotation(aClass.getSuperclass(), RoundingLog.class) : annotation;
        if (null == annotation) {
            log.error("<<< Fatal error, not support logging, go through execution without logs");
            return pjp.proceed();
        }
        StopWatch sw = annotation.carePerformance() ? new StopWatch() : null;

        LogTmpParams logTmpParams = new LogTmpParams(pjp.getSignature(), pjp.getArgs(), sw);
        // 1. input logs
        logTmpParams.beforeCallingLog();
        // 2. process
        Object proceed = pjp.proceed();
        // 3. output logs according to error
        logTmpParams.afterCallingLog(proceed);
        return proceed;
    }

}
