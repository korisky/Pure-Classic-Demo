package com.own.spring.demo.spring;

import com.own.spring.demo.anno.CgLibLog;
import com.own.spring.demo.anno.JdkLog;
import com.own.spring.demo.proxy.LogProxyCgLib;
import com.own.spring.demo.proxy.LogProxyJdk;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/**
 * Pitching in Spring
 *
 * @author Roylic
 * 2023/1/13
 */
@Component
public class SpringAnnotPostProcessor implements BeanPostProcessor {

    /**
     * For CgLib before / after bean initialization is all good, but could not use twice
     */

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        // cglib enhancer -> Method Interceptor
        if (bean.getClass().isAnnotationPresent(CgLibLog.class) || bean.getClass().getSuperclass().isAnnotationPresent(CgLibLog.class)) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(bean.getClass());
            enhancer.setCallback(new LogProxyCgLib());
            return enhancer.create();
        }

        // jdk Invocation Handler
//        if (bean.getClass().isAnnotationPresent(JdkLog.class) || bean.getClass().getSuperclass().isAnnotationPresent(JdkLog.class)) {
//            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(), new LogProxyJdk(bean));
//        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        // cglib enhancer -> Method Interceptor
//        if (bean.getClass().isAnnotationPresent(CgLibLog.class) || bean.getClass().getSuperclass().isAnnotationPresent(CgLibLog.class)) {
//            Enhancer enhancer = new Enhancer();
//            enhancer.setSuperclass(bean.getClass());
//            enhancer.setCallback(new LogProxyCgLib());
//            return enhancer.create();
//        }

        return bean;
    }
}
