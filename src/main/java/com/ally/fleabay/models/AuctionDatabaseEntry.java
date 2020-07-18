package com.ally.fleabay.models;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class AuctionDatabaseEntry {
    @Id
    ObjectId id;
    double reservePrice;
    double currentBid;
    Item item;
}
