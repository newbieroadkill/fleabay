package com.ally.fleabay.controllers;

import com.ally.fleabay.exceptions.BiddingRetriesExhaustedException;
import com.ally.fleabay.models.bid.BidRequest;
import com.ally.fleabay.models.auction.AuctionDatabaseEntry;
import com.ally.fleabay.repositories.AuctionRepository;
import com.ally.fleabay.repositories.BidRepostiory;
import com.ally.fleabay.services.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class BidController {

    private static final int MAX_ATTEMPTS = 3;
    @Autowired
    BidService bidService;

    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    BidRepostiory bidRepostiory;

    @PostMapping("/bids")
    void createAuction(@Valid @RequestBody BidRequest bidRequest){
        bidService.logBid(bidRequest);
        AuctionDatabaseEntry result = null;
        for(int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
            try{
                result = bidService.processBidRequest(bidRequest);
                attempts = MAX_ATTEMPTS;
            } catch (OptimisticLockingFailureException ex){ }
        }

        if(result == null){
            throw new BiddingRetriesExhaustedException("Reached max retries");
        }
    }
}
