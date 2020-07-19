package com.ally.fleabay.models.auction;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetAuctionsResponse {
    List<Auction> items = new ArrayList<>();
}
