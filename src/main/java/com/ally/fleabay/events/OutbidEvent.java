package com.ally.fleabay.events;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OutbidEvent extends ApplicationEvent {
    String auctionItemId;
    String bidderName;

    public OutbidEvent(Object source, String auctionItemId, String bidderName) {
        super(source);
        setAuctionItemId(auctionItemId);
        setBidderName(bidderName);
    }
}
