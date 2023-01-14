package com.own.spring.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BaseController {


    protected String baseStuff(String outer) {
        return outer.concat("_base");
    }

}
