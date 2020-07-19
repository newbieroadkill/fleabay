package com.ally.fleabay.events;

import org.springframework.context.ApplicationEvent;

public class OutbidEvent extends ApplicationEvent {

    public OutbidEvent(Object source) {
        super(source);
    }
}
