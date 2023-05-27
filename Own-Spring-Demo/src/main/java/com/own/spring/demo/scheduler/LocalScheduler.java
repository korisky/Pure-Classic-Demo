package com.own.spring.demo.scheduler;

import com.own.spring.demo.events.CustomSpringEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalScheduler {

    private final CustomSpringEventPublisher eventPublisher;

    @Scheduled(cron = "0/10 * * * * ?")
    public void triggerEvent() {
        eventPublisher.publishCustomEvent("Haha");
    }

}
