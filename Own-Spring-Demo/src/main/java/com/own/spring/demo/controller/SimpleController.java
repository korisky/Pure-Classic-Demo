package com.own.spring.demo.controller;

import com.own.spring.demo.anno.CgLibLog;
import com.own.spring.demo.param.InputParam;
import com.own.spring.demo.service.SayHello;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

    @Autowired
    private ApplicationContext applicationContext;


//    public SimpleController(TestConfig testConfig) {
//        super(testConfig);
//    }

    @PostMapping("/post")
    public String getMapping(@RequestBody InputParam input) {
        return baseStuff("simple") + input.getName() + " yeah";
    }

    @GetMapping("/service/log")
    public void testServiceLog() {
        SayHello bean = applicationContext.getBean(SayHello.class);
        System.out.println(bean.sayHello());
    }

}
