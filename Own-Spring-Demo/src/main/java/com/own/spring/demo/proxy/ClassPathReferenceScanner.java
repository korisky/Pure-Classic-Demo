package com.own.spring.demo.proxy;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ClassPathReferenceScanner extends ClassPathBeanDefinitionScanner {


    private BeanDefinitionRegistry registry;

    private Class<? extends Annotation> annotationClass;


    public ClassPathReferenceScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
        this.registry = registry;
    }


    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    /**
     * For registration filter
     */
    public void registerFilters() {
        if (this.annotationClass != null) {
            addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
        }

        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    /**
     * Only care interface
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    /**
     * Scan for inject custom logic
     */
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        processBeanDefinitions(beanDefinitionHolders);
        return beanDefinitionHolders;
    }

    /**
     * Custom logic for might register extra bean definition
     */
    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        for (BeanDefinitionHolder holder : beanDefinitionHolders) {
            String className = holder.getBeanDefinition().getBeanClassName();

            // building own bean definition
            BeanDefinitionBuilder bdBuilder = BeanDefinitionBuilder.genericBeanDefinition(className);
            GenericBeanDefinition proxyBeanDefinition = (GenericBeanDefinition) bdBuilder.getRawBeanDefinition();
            proxyBeanDefinition.getPropertyValues().add("interfaceClass", className);
            proxyBeanDefinition.setBeanClass(ProxyBeanFactory.class);
            proxyBeanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);

            // register
            this.registry.registerBeanDefinition(holder.getBeanName(), proxyBeanDefinition);

        }
    }

}
