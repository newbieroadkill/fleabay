package com.ally.fleabay.bid;

import com.ally.fleabay.models.bid.BidRequest;
import com.ally.fleabay.models.Item;
import com.ally.fleabay.models.auction.AuctionDatabaseEntry;
import com.ally.fleabay.repositories.AuctionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureDataMongo
@AutoConfigureMockMvc
public class UnsuccessfulBidTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuctionRepository auctionRepository;

    AuctionDatabaseEntry auctionDatabaseEntry;

    BidRequest bidRequest;
    @BeforeEach
    public void setup(){
        auctionRepository.deleteAll();
        auctionDatabaseEntry = auctionRepository.save(AuctionDatabaseEntry.builder()
                .reservePrice(BigDecimal.TEN.setScale(2, RoundingMode.DOWN))
                .currentBid(BigDecimal.ZERO.setScale(2, RoundingMode.DOWN))
                .item(Item.builder().itemId("id").description("description").build()).build());

        bidRequest = BidRequest.builder().auctionItemId(auctionDatabaseEntry.getId().toHexString())
                .maxAutoBidAmount(new BigDecimal("1.00"))
                .bidderName("Bob Al'Hashib").build();
    }

    @Test
    public void whenInvalidRequestComesIn_400WhenAuctionItemIdMissing() throws Exception {
        mockMvc.perform(post("/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bidRequest).replace("\"auctionItemId\":\"" +
                        auctionDatabaseEntry.getId().toHexString() + "\",", ""))
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void whenInvalidRequestComesIn_400WhenMaxAutoBidAmountMissing() throws Exception {
        mockMvc.perform(post("/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bidRequest).replace("\"maxAutoBidAmount\":1.00,", ""))
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void whenInvalidRequestComesIn_400WhenBidderNameMissing() throws Exception {
        mockMvc.perform(post("/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bidRequest).replace(",\"bidderName\":\"Bob Al'Hashib\"", ""))
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void whenRequestComesInWithAuctionItemIdThatDoesNotExist_Return404() throws Exception {
        auctionRepository.deleteAll();

        mockMvc.perform(post("/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bidRequest))
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void whenRequestComesInWithSameMaxBidAsCurrentMaxBid_UpdateCurrentBidButNotBidderName(){}
}
