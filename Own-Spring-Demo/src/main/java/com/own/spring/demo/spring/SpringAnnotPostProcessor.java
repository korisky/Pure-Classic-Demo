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
 * https://www.javai.net/post/202207/java-proxy/#31-jdk-dynamic-proxy-mechanism
 *
 * CgLib Proxy -> if target class or it's super class do not contain non-arg constructor,
 * CgLib Proxy would throw error
 * https://blog.csdn.net/baidu_28610773/article/details/82926075
 * Reason: CgLib采用的字节码技术, 需要生成被代理类的子类, 再织入逻辑. 但字节码技术与Spring容器处于不同维度
 *      (字节码在JVM层面), 生成代理时无法通过Spring自动注入, 所以没有空Constructor时会导致直接无法启动
 *
 *
 *
 * JDK Proxy -> could not be used on creating Controller Proxy -> got 404
 * https://www.jianshu.com/p/d262be7f2c88
 * Reason: jdk动态代理采用和被代理的类实现同一个接口的方式来进行代理。但是生成的代理类没有@Controller注解,
 *      以及方法上的@GetMapping等方法, 所以springMvc扫描bean, 针对Controller和Controller方法的映射关系时,
 *      会把这个Jdk代理的类认为不需要生成映射关系, 也就导致请求地址无法访问到Controller方法导致404. 使用GcLib
 *      由于采用的方法是通过字节码方式生成一个被代理类的子类, 所以springMvc还是会为它处理好映射关系
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

//        // cglib enhancer -> Method Interceptor
//        if (bean.getClass().isAnnotationPresent(CgLibLog.class) || bean.getClass().getSuperclass().isAnnotationPresent(CgLibLog.class)) {
//            Enhancer enhancer = new Enhancer();
//            enhancer.setSuperclass(bean.getClass());
//            enhancer.setCallback(new LogProxyCgLib());
//            return enhancer.create();
//        }

        // jdk Invocation Handler
//        if (bean.getClass().isAnnotationPresent(JdkLog.class) || bean.getClass().getSuperclass().isAnnotationPresent(JdkLog.class)) {
//            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(), new LogProxyJdk(bean));
//        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

//        // cglib enhancer -> Method Interceptor
//        if (bean.getClass().isAnnotationPresent(CgLibLog.class) || bean.getClass().getSuperclass().isAnnotationPresent(CgLibLog.class)) {
//            Enhancer enhancer = new Enhancer();
//            enhancer.setSuperclass(bean.getClass());
//            enhancer.setCallback(new LogProxyCgLib());
//            return enhancer.create();
//        }

        return bean;
    }
}
