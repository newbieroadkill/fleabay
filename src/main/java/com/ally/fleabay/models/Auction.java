package com.ally.fleabay.models;

import com.ally.fleabay.utils.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Auction {
    String id;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    BigDecimal reservePrice;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    BigDecimal currentBid;

    Item item;
}
