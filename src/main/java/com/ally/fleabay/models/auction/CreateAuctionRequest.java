package com.ally.fleabay.models.auction;

import com.ally.fleabay.models.Item;
import com.ally.fleabay.utils.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
public class CreateAuctionRequest {
    @NotNull
    @DecimalMin("1.00")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    BigDecimal reservePrice;

    @Valid
    Item item;
}
