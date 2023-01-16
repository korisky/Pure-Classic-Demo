package com.own.anno.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

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
        LogTmpParams logTmpParams = new LogTmpParams(pjp.getSignature(), pjp.getArgs());
        // 1. input logs
        logTmpParams.beforeCallingLog();
        // 2. process
        Object proceed = pjp.proceed();
        // 3. output logs according to error
        logTmpParams.afterCallingLog(proceed);
        return proceed;
    }

}
