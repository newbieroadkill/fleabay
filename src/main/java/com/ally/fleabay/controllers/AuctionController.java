package com.ally.fleabay.controllers;

import com.ally.fleabay.models.Auction;
import com.ally.fleabay.models.CreateAuctionRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuctionController {

    @PostMapping("")
    Auction createAuction(@Valid @RequestBody CreateAuctionRequest request){

    }

}
