package com.own.spring.demo.controller;

import com.own.spring.demo.config.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BaseController {

//    private final String CONFIG;
//
//    public BaseController(TestConfig testConfig) {
//        this.CONFIG = testConfig.getConfigName();
//    }

    protected String baseStuff(String outer) {
//        return outer.concat(CONFIG).concat("_base");
        return outer.concat("_base");
    }

}
