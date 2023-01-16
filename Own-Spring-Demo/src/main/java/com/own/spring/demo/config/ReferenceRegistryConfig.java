//package com.own.spring.demo.config;
//
//import com.own.spring.demo.anno.BeanDefinitionLog;
//import com.own.spring.demo.spring.BeanDefinitionLogRegistryPostProcessor;
//import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class ReferenceRegistryConfig {
//    @Bean
//    public BeanDefinitionRegistryPostProcessor postProcessor() {
//        BeanDefinitionLogRegistryPostProcessor referenceRegistryConfig = new BeanDefinitionLogRegistryPostProcessor();
//        referenceRegistryConfig.setAnnotationClass(BeanDefinitionLog.class);
//        referenceRegistryConfig.setBasePackage("com.own.spring.demo");
//        return referenceRegistryConfig;
//    }
//}
