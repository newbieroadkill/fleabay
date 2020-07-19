package com.ally.fleabay.controllers;

import com.ally.fleabay.exceptions.AuctionNotFoundException;
import com.ally.fleabay.models.BidDatabaseEntry;
import com.ally.fleabay.models.BidRequest;
import com.ally.fleabay.models.auction.AuctionDatabaseEntry;
import com.ally.fleabay.repositories.AuctionRepository;
import com.ally.fleabay.repositories.BidRepostiory;
import com.ally.fleabay.utils.MongoUtils;
import org.bson.BsonTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
public class BidController {

    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    BidRepostiory bidRepostiory;

    @PostMapping("/bids")
    void createAuction(@Valid @RequestBody BidRequest bidRequest){
         Optional<AuctionDatabaseEntry> auctionDatabaseEntryOptional = auctionRepository.findById(MongoUtils.getObjectIdFromString(bidRequest.getAuctionItemId()));

        bidRepostiory.save(BidDatabaseEntry.builder().auctionItemId(bidRequest.getAuctionItemId())
                .bidderName(bidRequest.getBidderName())
                .maxAutoBidAmount(bidRequest.getMaxAutoBidAmount()).build());

        if(auctionDatabaseEntryOptional.isPresent()){
            AuctionDatabaseEntry auctionDatabaseEntry = auctionDatabaseEntryOptional.get();
            if(auctionDatabaseEntry.getMaximumBid() == null || bidRequest.getMaxAutoBidAmount().compareTo(auctionDatabaseEntry.getMaximumBid()) > 0){
                auctionDatabaseEntry.setBidderName(bidRequest.getBidderName());
            }
            auctionDatabaseEntry.setCurrentBid(calculateCurrentBid(bidRequest, auctionDatabaseEntry));
            auctionDatabaseEntry.setMaximumBid(bidRequest.getMaxAutoBidAmount());
            auctionRepository.save(auctionDatabaseEntry);
        } else {
            throw new AuctionNotFoundException();
        }
    }

    private BigDecimal calculateCurrentBid(BidRequest bidRequest, AuctionDatabaseEntry auctionDatabaseEntry){
        BigDecimal highestNecessaryBidValue = auctionDatabaseEntry.getReservePrice();
        if(auctionDatabaseEntry.getMaximumBid() != null && auctionDatabaseEntry.getMaximumBid().compareTo(highestNecessaryBidValue) > 0) {
            highestNecessaryBidValue = highestNecessaryBidValue.max(auctionDatabaseEntry.getMaximumBid().add(new BigDecimal("1.00")));
        }
        return bidRequest.getMaxAutoBidAmount().min(highestNecessaryBidValue);
    }
}
