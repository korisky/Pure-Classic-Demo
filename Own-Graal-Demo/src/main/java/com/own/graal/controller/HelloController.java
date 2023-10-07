package com.own.graal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller
 *
 * @author Roylic
 * 2023/10/7
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("/graal")
    public String get_endpoint() {
        return "Graal Working";
    }

}
