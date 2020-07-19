package com.ally.fleabay.models.auction;

import com.ally.fleabay.models.Item;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
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
    BigDecimal maximumBid;
    String bidderName;
    Item item;
    @Version
    Long version;
}
