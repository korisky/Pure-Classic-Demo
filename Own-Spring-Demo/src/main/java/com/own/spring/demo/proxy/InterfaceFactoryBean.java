package com.own.spring.demo.proxy;

import lombok.Data;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @author Roylic
 * 2023/1/13
 */
@Data
public class InterfaceFactoryBean<T> implements FactoryBean<T> {

    private Class<T> interfaceClass;

    private String params;

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new DynamicInvocationHandler(params));
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
