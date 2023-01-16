package com.own.anno.demo.config;

import com.own.anno.demo.aspect.RoundingLogAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Roylic
 * 2023/1/16
 */
@Configuration
public class InjectionConfig {

    @Bean
    public RoundingLogAspect roundingLogAspect() {
        return new RoundingLogAspect();
    }
}
