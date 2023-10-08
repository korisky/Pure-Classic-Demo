package com.own.controller;

import org.springframework.nativex.hint.AotProxyHint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Roylic
 * 2023/10/8
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    @AotProxyHint
    @GetMapping("/graal")
    public String hello_graal() {
        return "Hello, graalvm";
    }
}
