package com.own.spring.demo.spring;

import com.own.spring.demo.anno.JdkLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @author Roylic
 * 2023/1/13
 */
@Slf4j
@Component
public class SpringAnnotBeanFactoryPostProcessor implements BeanClassLoaderAware, BeanFactoryPostProcessor {


    private ClassLoader classLoader;


    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // Bean实例化前, 对所有BeanDefinition进行遍历
//        for (String bdName : beanFactory.getBeanDefinitionNames()) {
//            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(bdName);
//            String className = beanDefinition.getBeanClassName();
//            if (null != className) {
//                Class<?> aClass = ClassUtils.resolveClassName(className, this.classLoader);
//                if (aClass.isAnnotationPresent(JdkLog.class)) {
//                    System.out.println();
//                }
//            }
//        }
//        // 对Map找到的Field进行遍历, 将所有已经被Proxy的BeanDefinition的都register到registry上
//        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
//        this.rpcRefBeanDefinition.forEach((name, definition) -> {
//            if (context.containsBean(name)) {
//                log.warn("Already register Bean with name: " + name);
//            }
//            registry.registerBeanDefinition(name, definition);
//            log.info("Register Bean with name: " + name + " successfully");
//        });
    }


}
