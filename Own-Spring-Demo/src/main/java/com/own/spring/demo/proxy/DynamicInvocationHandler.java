package com.own.spring.demo.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Roylic
 * 2023/1/13
 */
@Slf4j
public class DynamicInvocationHandler implements InvocationHandler {

    private String params;

    public DynamicInvocationHandler(String params) {
        this.params = params;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args.length > 0) {
            log.debug(method.getName() + ", " + args[0].toString() + ", " + params);
        }
        return "invocation return";
    }
}
