package com.own.spring.demo.proxy;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Log Proxy Class
 *
 * @author Roylic
 * 2023/1/13
 */
@Slf4j
public class LogProxyCgLib implements MethodInterceptor {

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        // before logging
        beforeLogging(o.getClass().getSimpleName(), method, objects);
        // proceed
        return methodProxy.invokeSuper(o, objects);
    }

    private void beforeLogging(String simpleEnhancedClassName, Method method, Object[] params) {
        // not care of void methods
        if (null == params || params.length == 0) {
            return;
        }
        // only care public
        if (method.getModifiers() == 1) {
            Object loggingArgs = params;
            String proxiedClassName = simpleEnhancedClassName.split("\\$\\$")[0];
            if (proxiedClassName.contains("controller") || proxiedClassName.contains("Controller")) {
                loggingArgs = params[0];
            }
            log.info("[Proxy_CgLib_{}] input on [{}] with params:[{}]", proxiedClassName, method.getName(), JSONObject.toJSONString(loggingArgs));
        }
    }


}
