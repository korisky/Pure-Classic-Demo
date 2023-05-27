package com.own.spring.demo.events;

import org.springframework.context.ApplicationEvent;

/**
 * Custom Spring Event
 */
public class CustomSpringEvent extends ApplicationEvent {

    // the msg here is about the event's payload
    private String msg;

    public CustomSpringEvent(Object source, String msg) {
        super(source);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
