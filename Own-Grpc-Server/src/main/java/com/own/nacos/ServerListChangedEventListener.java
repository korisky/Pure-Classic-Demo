package com.own.nacos;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.client.naming.event.ServerListChangedEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class ServerListChangedEventListener extends Subscriber<ServerListChangedEvent> {

    @Override
    public Class<? extends Event> subscribeType() {
        return ServerListChangedEvent.class;
    }

    @Override
    public void onEvent(ServerListChangedEvent serverListChangedEvent) {
        log.info(">>> Received Nacos ServerListChangedEvent: {}", JSON.toJSONString(serverListChangedEvent));
        System.out.println();
    }

    @PostConstruct
    public void postConstruct() {
        NotifyCenter.registerSubscriber(this);
    }


}
