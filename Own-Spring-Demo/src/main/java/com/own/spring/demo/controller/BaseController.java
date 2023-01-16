package com.own.spring.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BaseController {


    private String CONFIG = "default";


    public void setCONFIG(String config) {
        this.CONFIG = config;
    }

    protected String baseStuff(String outer) {
        return outer.concat(CONFIG).concat("_base");
//        return outer.concat("_base");
    }

}
