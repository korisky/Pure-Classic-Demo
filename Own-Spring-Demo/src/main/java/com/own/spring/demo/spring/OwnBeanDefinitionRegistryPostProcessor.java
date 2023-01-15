package com.own.spring.demo.spring;

import com.own.spring.demo.anno.CgLibLog;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.stereotype.Component;

/**
 * Usually, BeanDefinitionRegistryPostProcessor is for adding customized bean that we need to construct by some reason,
 * not for replacing or enhancing bean with proxy or similar stuff
 * <p>
 * https://blog.csdn.net/qq_20597727/article/details/80996190
 * https://stackoverflow.com/questions/39105082/get-annotations-of-bean-in-beanpostprocessor-implementation
 * https://www.logicbig.com/tutorials/spring-framework/spring-core/bean-definition.html
 *
 * @author Roylic
 * 2023/1/13
 */
@Component
public class OwnBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        for (String beanDefinitionName : beanDefinitionRegistry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanDefinitionName);
            // take care of AnnotatedBeanDefinition
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                // get metadata for checking specific annotation
                if (((ScannedGenericBeanDefinition) beanDefinition).getMetadata()
                        .isAnnotated(CgLibLog.class.getName())) {
                    // then can take care of these bean's definition, or according to this, register extra beans
                }
            }
        }
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
