package com.ally.fleabay.models;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
@Data
@Builder
public class AuctionDatabaseEntry {
    @Id
    ObjectId id;
    BigDecimal reservePrice;
    BigDecimal currentBid;
    String bidderName;
    Item item;
}
