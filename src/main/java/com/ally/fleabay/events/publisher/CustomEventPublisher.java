package com.ally.fleabay.events.publisher;

import com.ally.fleabay.events.OutbidEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CustomEventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishOutbidEvent(OutbidEvent outbidEvent){
        applicationEventPublisher.publishEvent(outbidEvent);
    }
}
