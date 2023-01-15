package com.own.spring.demo.spring;

import com.own.spring.demo.anno.CgLibLog;
import com.own.spring.demo.proxy.ClassPathReferenceScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * https://blog.csdn.net/sz85850597/article/details/103843694
 *
 * @author Roylic
 * 2023/1/13
 */
@Component
public class ReferenceRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, InitializingBean,
        ApplicationContextAware, BeanNameAware {

    private String basePackage;

    private Class<? extends Annotation> annotationClass;

    private ApplicationContext applicationContext;

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void setBeanName(String name) {
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {

        ClassPathReferenceScanner scanner = new ClassPathReferenceScanner(beanDefinitionRegistry);
        scanner.setAnnotationClass(this.annotationClass);
        scanner.setResourceLoader(this.applicationContext);
        scanner.registerFilters();
        scanner.scan(basePackage);

    }


}
