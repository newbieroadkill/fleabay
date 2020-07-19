package com.ally.fleabay.services;

import com.ally.fleabay.exceptions.AuctionNotFoundException;
import com.ally.fleabay.models.BidDatabaseEntry;
import com.ally.fleabay.models.BidRequest;
import com.ally.fleabay.models.auction.AuctionDatabaseEntry;
import com.ally.fleabay.repositories.AuctionRepository;
import com.ally.fleabay.repositories.BidRepostiory;
import com.ally.fleabay.utils.MongoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BidService {
    
    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    BidRepostiory bidRepostiory;

    public void logBid(BidRequest bidRequest){
        bidRepostiory.save(BidDatabaseEntry.builder().auctionItemId(bidRequest.getAuctionItemId())
                .bidderName(bidRequest.getBidderName())
                .maxAutoBidAmount(bidRequest.getMaxAutoBidAmount()).build());
    }

    public AuctionDatabaseEntry processBidRequest(BidRequest bidRequest) {
        Optional<AuctionDatabaseEntry> auctionDatabaseEntryOptional = auctionRepository.findById(MongoUtils.getObjectIdFromString(bidRequest.getAuctionItemId()));

        if(auctionDatabaseEntryOptional.isPresent()){
            AuctionDatabaseEntry auctionDatabaseEntry = auctionDatabaseEntryOptional.get();
            if(auctionDatabaseEntry.getMaximumBid() == null || bidRequest.getMaxAutoBidAmount().compareTo(auctionDatabaseEntry.getMaximumBid()) > 0){
                auctionDatabaseEntry.setBidderName(bidRequest.getBidderName());
            }
            auctionDatabaseEntry.setCurrentBid(calculateCurrentBid(bidRequest, auctionDatabaseEntry));
            auctionDatabaseEntry.setMaximumBid(bidRequest.getMaxAutoBidAmount());
            return auctionRepository.save(auctionDatabaseEntry);
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
