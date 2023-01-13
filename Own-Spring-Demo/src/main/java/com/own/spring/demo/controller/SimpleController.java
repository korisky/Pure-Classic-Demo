package com.own.spring.demo.controller;

import com.own.spring.demo.anno.RoundingLog;
import com.own.spring.demo.param.InputParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author Roylic
 * 2023/1/13
 */
@Slf4j
@RestController
@RequestMapping("/simple")
@RoundingLog
public class SimpleController {


    @PostMapping("/post")
    public String getMapping(@RequestBody InputParam input) {
        return "yeah";
    }

}
