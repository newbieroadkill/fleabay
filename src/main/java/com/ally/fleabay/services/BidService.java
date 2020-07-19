package com.ally.fleabay.services;

import com.ally.fleabay.events.OutbidEvent;
import com.ally.fleabay.events.publisher.CustomEventPublisher;
import com.ally.fleabay.exceptions.AuctionNotFoundException;
import com.ally.fleabay.models.auction.Auction;
import com.ally.fleabay.models.bid.BidDatabaseEntry;
import com.ally.fleabay.models.bid.BidRequest;
import com.ally.fleabay.models.auction.AuctionDatabaseEntry;
import com.ally.fleabay.repositories.AuctionRepository;
import com.ally.fleabay.repositories.BidRepostiory;
import com.ally.fleabay.utils.MongoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BidService {
    
    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    BidRepostiory bidRepostiory;

    @Autowired
    CustomEventPublisher customEventPublisher;

    public void logBid(BidRequest bidRequest){
        bidRepostiory.save(BidDatabaseEntry.builder().auctionItemId(bidRequest.getAuctionItemId())
                .bidderName(bidRequest.getBidderName())
                .maxAutoBidAmount(bidRequest.getMaxAutoBidAmount()).build());
    }

    public AuctionDatabaseEntry processBidRequest(BidRequest bidRequest) {
        Optional<AuctionDatabaseEntry> auctionDatabaseEntryOptional = auctionRepository.findById(MongoUtils.getObjectIdFromString(bidRequest.getAuctionItemId()));
        String outbidName = "";

        if(auctionDatabaseEntryOptional.isPresent()){
            AuctionDatabaseEntry auctionDatabaseEntry = auctionDatabaseEntryOptional.get();
            if(auctionDatabaseEntry.getMaximumBid() == null || bidRequest.getMaxAutoBidAmount().compareTo(auctionDatabaseEntry.getMaximumBid()) > 0){
                outbidName = auctionDatabaseEntry.getBidderName() != null ? auctionDatabaseEntry.getBidderName() : "";
                auctionDatabaseEntry.setBidderName(bidRequest.getBidderName());
            }
            auctionDatabaseEntry.setCurrentBid(calculateCurrentBid(bidRequest, auctionDatabaseEntry));
            auctionDatabaseEntry.setMaximumBid(bidRequest.getMaxAutoBidAmount());
            AuctionDatabaseEntry saveResult = auctionRepository.save(auctionDatabaseEntry);
            if(!outbidName.isEmpty()){
                customEventPublisher.publishOutbidEvent(new OutbidEvent(this, auctionDatabaseEntry.getId().toHexString() ,outbidName));
            }
            return saveResult;
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
