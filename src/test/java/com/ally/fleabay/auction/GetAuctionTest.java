package com.ally.fleabay.auction;

import com.ally.fleabay.models.Auction;
import com.ally.fleabay.models.AuctionDatabaseEntry;
import com.ally.fleabay.models.CreateAuctionRequest;
import com.ally.fleabay.models.Item;
import com.ally.fleabay.repositories.AuctionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureDataMongo
@AutoConfigureMockMvc
public class GetAuctionTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuctionRepository auctionRepository;

    AuctionDatabaseEntry auctionDatabaseEntry;

    @BeforeEach
    public void setup(){
        auctionDatabaseEntry = auctionRepository.save(AuctionDatabaseEntry.builder()
                .reservePrice(BigDecimal.TEN.setScale(2, RoundingMode.DOWN))
                .currentBid(BigDecimal.ZERO.setScale(2, RoundingMode.DOWN))
                .item(Item.builder().itemId("id").description("description").build()).build());
    }

    @Test
    public void whenProperIdentifierPassedIntoGetAuctionCallAndAuctionRecordExists_ReturnAuction() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/auctionItems/" + auctionDatabaseEntry.getId().toHexString()))
                .andExpect(status().isOk()).andReturn();

        Auction responseAuction = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), Auction.class);

        assertEquals(auctionDatabaseEntry.getReservePrice(), responseAuction.getReservePrice());
        assertEquals(auctionDatabaseEntry.getCurrentBid(), responseAuction.getCurrentBid());
        assertEquals(auctionDatabaseEntry.getItem().getItemId(), responseAuction.getItem().getItemId());
        assertEquals(auctionDatabaseEntry.getItem().getDescription(), responseAuction.getItem().getDescription());
    }

    @Test
    public void whenProperIdentifierPassedIntoGetAuctionCallAndAuctionRecordDoesNotExist_Return404() throws Exception {
        mockMvc.perform(get("/auctionItems/" + (new ObjectId().toHexString())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenMalformedIdentifierPassedIntoGetAuctionCallAndAuctionRecordDoesNotExist_Return404() throws Exception {
        mockMvc.perform(get("/auctionItems/poop"))
                .andExpect(status().isBadRequest());
    }
}
