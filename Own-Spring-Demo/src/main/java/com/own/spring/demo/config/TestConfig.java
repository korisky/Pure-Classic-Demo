package com.own.spring.demo.config;

import com.own.spring.demo.controller.BaseController;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "test")
public class TestConfig {

    @Autowired
    private BaseController baseController;

    private String configName;

    @PostConstruct
    public void postConstruct() {
        baseController.setCONFIG(configName);
    }
}
