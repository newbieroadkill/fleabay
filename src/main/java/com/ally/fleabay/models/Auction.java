package com.ally.fleabay.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Auction {
    String id;
    double reservePrice;
    double currentBid;
    Item item;
}
