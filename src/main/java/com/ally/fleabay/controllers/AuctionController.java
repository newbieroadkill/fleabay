package com.ally.fleabay.controllers;

import com.ally.fleabay.models.Auction;
import com.ally.fleabay.models.AuctionDatabaseEntry;
import com.ally.fleabay.models.CreateAuctionRequest;
import com.ally.fleabay.models.Item;
import com.ally.fleabay.repositories.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

@RestController
public class AuctionController {

    @Autowired
    AuctionRepository auctionRepository;

    @PostMapping("/auctionItems")
    Auction createAuction(@Valid @RequestBody CreateAuctionRequest request){
        AuctionDatabaseEntry auctionDatabaseEntry = AuctionDatabaseEntry.builder()
                .currentBid(BigDecimal.ZERO.setScale(2))
                .reservePrice(request.getReservePrice())
                .item(request.getItem()).build();

        auctionDatabaseEntry = auctionRepository.save(auctionDatabaseEntry);

        return Auction.builder().id(auctionDatabaseEntry.getId().toHexString())
                .currentBid(auctionDatabaseEntry.getCurrentBid())
                .reservePrice(auctionDatabaseEntry.getReservePrice())
                .item(auctionDatabaseEntry.getItem()).build();
    }

    @GetMapping("/auctionItems")
    List<Auction> getAuctions() {
         return new ArrayList<Auction>();
    }

    @GetMapping("/auctionItems/{auctionItemId}")
    Auction getAuction()  {
        return Auction.builder().build();
    }

}
