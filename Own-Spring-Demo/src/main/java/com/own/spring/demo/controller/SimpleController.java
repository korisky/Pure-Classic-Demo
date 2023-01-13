package com.own.spring.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Roylic
 * 2023/1/13
 */
@Slf4j
@RestController
@RequestMapping("/simple")
public class SimpleController {


    @GetMapping("/get")
    public String getMapping(String input) {
        log.info("In SimpleController, with input:{}", input);
        return input.concat(" hahahaha");
    }

}
