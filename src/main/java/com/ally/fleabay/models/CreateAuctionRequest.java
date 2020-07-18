package com.ally.fleabay.models;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class CreateAuctionRequest {
    @NotNull
    double reservePrice;

    @NotNull
    Item item;
}
