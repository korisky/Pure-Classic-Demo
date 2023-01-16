package com.own.spring.demo.proxy;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Log Proxy Class
 *
 * @author Roylic
 * 2023/1/13
 */
@Slf4j
public class LogProxyJdk implements InvocationHandler {

    private Object storing;

    public LogProxyJdk(Object storing) {
        this.storing = storing;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        beforeLogging(proxy.getClass().getSimpleName(), method, args);
        return method.invoke(storing, args);
    }

    private void beforeLogging(String simpleEnhancedClassName, Method method, Object[] params) {
        if (method.getModifiers() == 1) {
            Object loggingArgs = params;
            String proxiedClassName = simpleEnhancedClassName.split("\\$\\$")[0];
            if (proxiedClassName.contains("controller") || proxiedClassName.contains("Controller")) {
                loggingArgs = params[0];
            }
            log.debug("[Proxy_Jdk_{}] input on [{}] with params:[{}]", proxiedClassName, method.getName(), JSONObject.toJSONString(loggingArgs));
        }
    }
}
