package com.own.spring.demo.spring;

import com.own.spring.demo.anno.RoundingLog;
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


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//        // take care of rounding log
        if (bean.getClass().isAnnotationPresent(RoundingLog.class)) {
            // cglib enhancer
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(bean.getClass());
            enhancer.setCallback(new LogProxyCgLib());
            return enhancer.create();
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // take care of rounding log
        if (bean.getClass().getSimpleName().contains("InteractionController") || bean.getClass().getSimpleName().contains("interactionController")) {
            System.out.println();
        }
        // must take care super class cause Spring might use Cglib to proxy instances
        if (bean.getClass().isAnnotationPresent(RoundingLog.class) || bean.getClass().getSuperclass().isAnnotationPresent(RoundingLog.class)) {
            // jdk enhancer
            System.out.println("Proxy");
            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(), new LogProxyJdk(bean));
        }
        return bean;
    }
}
