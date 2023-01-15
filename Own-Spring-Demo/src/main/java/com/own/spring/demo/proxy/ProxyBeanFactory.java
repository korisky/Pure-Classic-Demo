package com.own.spring.demo.proxy;

import lombok.Data;
import org.springframework.beans.factory.FactoryBean;

@Data
public class ProxyBeanFactory<T> implements FactoryBean<T> {

    private Class<T> interfaceClass;


    @Override
    public T getObject() {
        return (T) new HelloProxy().bind(interfaceClass);
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
