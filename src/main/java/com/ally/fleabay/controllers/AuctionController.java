package com.ally.fleabay.controllers;

import com.ally.fleabay.exceptions.AuctionNotFoundException;
import com.ally.fleabay.exceptions.InvalidObjectIdException;
import com.ally.fleabay.models.auction.Auction;
import com.ally.fleabay.models.auction.AuctionDatabaseEntry;
import com.ally.fleabay.models.auction.CreateAuctionRequest;
import com.ally.fleabay.models.auction.GetAuctionsResponse;
import com.ally.fleabay.repositories.AuctionRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

@RestController
public class AuctionController {

    @Autowired
    AuctionRepository auctionRepository;

    @PostMapping("/auctionItems")
    Auction createAuction(@Valid @RequestBody CreateAuctionRequest request){
        AuctionDatabaseEntry auctionDatabaseEntry = AuctionDatabaseEntry.builder()
                .currentBid(BigDecimal.ZERO.setScale(2, RoundingMode.DOWN))
                .reservePrice(request.getReservePrice().setScale(2, RoundingMode.DOWN))
                .item(request.getItem()).build();

        auctionDatabaseEntry = auctionRepository.save(auctionDatabaseEntry);

        return auctionFromDatabaseEntry(auctionDatabaseEntry);
    }

    @GetMapping("/auctionItems")
    GetAuctionsResponse getAuctions() {
         GetAuctionsResponse getAuctionsResponse = new GetAuctionsResponse();
        List<Auction> auctions = new ArrayList<>();
        for (AuctionDatabaseEntry auctionDatabaseEntry : auctionRepository.findAll()) {
            auctions.add(auctionFromDatabaseEntry(auctionDatabaseEntry));
        }
        getAuctionsResponse.setItems(auctions);
         return getAuctionsResponse;
    }

    @GetMapping("/auctionItems/{auctionItemId}")
    Auction getAuction(@PathVariable String auctionItemId)  {
        ObjectId auctionItemObjectId;
        try {
            auctionItemObjectId = new ObjectId(auctionItemId);
        } catch (IllegalArgumentException ex){
            throw new InvalidObjectIdException();
        }
        
        Optional<AuctionDatabaseEntry> auctionDatabaseEntryOptional = auctionRepository.findById(auctionItemObjectId);

        if(auctionDatabaseEntryOptional.isEmpty()){
            throw new AuctionNotFoundException();
        } else {
           return auctionFromDatabaseEntry(auctionDatabaseEntryOptional.get());
        }
    }

    private Auction auctionFromDatabaseEntry(AuctionDatabaseEntry auctionDatabaseEntry){
         return Auction.builder().id(auctionDatabaseEntry.getId().toHexString())
                 .currentBid(auctionDatabaseEntry.getCurrentBid())
                 .bidderName(auctionDatabaseEntry.getBidderName())
                 .reservePrice(auctionDatabaseEntry.getReservePrice())
                 .item(auctionDatabaseEntry.getItem()).build();
    }

}
