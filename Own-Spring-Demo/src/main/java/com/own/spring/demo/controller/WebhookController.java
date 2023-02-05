package com.own.spring.demo.controller;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/webhook")
public class WebhookController {


    @PostMapping
    public ResponseEntity<Object> webhook_endpoint(@RequestHeader Map<String, String> headers,
                                                   @RequestBody String body) {
        log.debug("All Headers: {}", JSONObject.toJSONString(headers));
        log.debug("Webhook Event Log: {}", body);
        return ResponseEntity.ok().build();
    }


}
