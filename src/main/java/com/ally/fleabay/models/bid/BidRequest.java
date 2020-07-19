package com.ally.fleabay.models.bid;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
public class BidRequest {
    @NotNull
    String auctionItemId;

    @NotNull
    @DecimalMin("1.00")
    BigDecimal maxAutoBidAmount;

    @NotNull
    String bidderName;
}
