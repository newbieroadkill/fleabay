package com.ally.fleabay.models;

import com.ally.fleabay.utils.BigDecimalJsonSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Auction {
    String id;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    BigDecimal reservePrice;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    BigDecimal currentBid;

    String bidderName;

    Item item;
}
