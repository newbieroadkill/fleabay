package com.ally.fleabay.models;

import lombok.Builder;
import lombok.Data;;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
public class BidDatabaseEntry {
    @Id
    ObjectId id;

    @NotNull
    String auctionItemId;

    @NotNull
    @DecimalMin("1.00")
    BigDecimal maxAutoBidAmount;

    @NotNull
    String bidderName;

}
