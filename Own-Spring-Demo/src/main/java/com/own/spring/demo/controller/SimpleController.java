package com.own.spring.demo.controller;

import com.own.spring.demo.anno.CgLibLog;
import com.own.spring.demo.anno.JdkLog;
import com.own.spring.demo.config.TestConfig;
import com.own.spring.demo.param.InputParam;
import com.own.spring.demo.proxy.JdkProxyInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author Roylic
 * 2023/1/13
 */
@Slf4j
@RestController
@RequestMapping("/simple")
@CgLibLog
//@JdkLog
public class SimpleController extends BaseController {


//    public SimpleController(TestConfig testConfig) {
//        super(testConfig);
//    }

    @PostMapping("/post")
    public String getMapping(@RequestBody InputParam input) {
        return baseStuff("simple") + input.getName() + " yeah";
    }

}
