package com.own.spring.demo.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class HelloProxy implements InvocationHandler {


    private Class<?> interfaceClass;


    public Object bind(Class<?> cls) {
        this.interfaceClass = cls;
        return Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{interfaceClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("Proxy with method: {}", method.getName());
        return "Hello Proxy";
    }
}
