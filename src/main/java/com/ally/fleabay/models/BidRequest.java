package com.ally.fleabay.models;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BidRequest {
    @NotNull
    String auctionItemId;

    @NotNull
    @DecimalMin("1.00")
    BigDecimal maxAutoBidAmount;

    @NotNull
    String bidderName;
}
