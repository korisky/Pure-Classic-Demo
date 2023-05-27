package com.own.spring.demo.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSpringEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishCustomEvent(final String msg) {
        log.debug(">>> [CustomSpringEventPublisher] start construct custom event");
        CustomSpringEvent newEvent = new CustomSpringEvent(this, msg);
        applicationEventPublisher.publishEvent(newEvent);
    }
}
