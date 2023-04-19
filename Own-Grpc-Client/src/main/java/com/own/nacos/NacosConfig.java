package com.own.nacos;

import com.alibaba.nacos.common.notify.NotifyCenter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NacosConfig {
    public NacosConfig() {
        NotifyCenter.registerSubscriber(new NewInstanceEventListener());
    }
}