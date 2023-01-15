package com.own.spring.demo.config;

import com.own.spring.demo.anno.CgLibLog;
import com.own.spring.demo.spring.ReferenceRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReferenceRegistryConfig {
    @Bean
    public BeanDefinitionRegistryPostProcessor postProcessor() {
        ReferenceRegistryPostProcessor referenceRegistryConfig = new ReferenceRegistryPostProcessor();
        referenceRegistryConfig.setAnnotationClass(CgLibLog.class);
        referenceRegistryConfig.setBasePackage("com.own.spring.demo");
        return referenceRegistryConfig;
    }
}
