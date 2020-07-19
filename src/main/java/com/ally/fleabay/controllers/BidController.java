package com.ally.fleabay.controllers;

import com.ally.fleabay.models.Auction;
import com.ally.fleabay.models.BidRequest;
import com.ally.fleabay.models.CreateAuctionRequest;
import com.ally.fleabay.repositories.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class BidController {

    @Autowired
    AuctionRepository auctionRepository;

    @PostMapping("/bids")
    void createAuction(@Valid @RequestBody BidRequest request){

    }
}
