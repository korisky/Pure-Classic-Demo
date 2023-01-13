package com.own.spring.demo.spring;

import com.own.spring.demo.proxy.InterfaceFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Roylic
 * 2023/1/13
 */
@Component
public class OwnBeanDefRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        List<Class<?>> beanClazzs = (List<Class<?>>) beanDefinitionRegistry;
        for (Class<?> beanClazz : beanClazzs) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            definition.getPropertyValues().add("interfaceClass", beanClazz);
            definition.getPropertyValues().add("params", "配置信息");
            definition.setBeanClass(InterfaceFactoryBean.class);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            beanDefinitionRegistry.registerBeanDefinition(beanClazz.getSimpleName(), definition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
