package com.ally.fleabay.auction;

import com.ally.fleabay.models.Auction;
import com.ally.fleabay.models.AuctionDatabaseEntry;
import com.ally.fleabay.models.GetAuctionsResponse;
import com.ally.fleabay.models.Item;
import com.ally.fleabay.repositories.AuctionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureDataMongo
@AutoConfigureMockMvc
public class GetAuctionsTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuctionRepository auctionRepository;

    ArrayList<AuctionDatabaseEntry> auctionDatabaseEntries;

    @BeforeEach
    public void setup(){
        auctionDatabaseEntries = new ArrayList<>();

        auctionDatabaseEntries.add(auctionRepository.save(AuctionDatabaseEntry.builder()
                .reservePrice(BigDecimal.TEN.setScale(2, RoundingMode.DOWN))
                .currentBid(BigDecimal.ZERO.setScale(2, RoundingMode.DOWN))
                .item(Item.builder().itemId("id").description("description").build()).build()));

        auctionDatabaseEntries.add(auctionRepository.save(AuctionDatabaseEntry.builder()
                .reservePrice(new BigDecimal("99.99").setScale(2, RoundingMode.DOWN))
                .currentBid(new BigDecimal("12.00").setScale(2, RoundingMode.DOWN))
                .bidderName("My Mom")
                .item(Item.builder().itemId("otherId").description("otherDescription").build()).build()));

        auctionDatabaseEntries.add(auctionRepository.save(AuctionDatabaseEntry.builder()
                .reservePrice(new BigDecimal("999.99").setScale(2, RoundingMode.DOWN))
                .currentBid(new BigDecimal("1200.00").setScale(2, RoundingMode.DOWN))
                .bidderName("Your Mom")
                .item(Item.builder().itemId("anotherId").description("anotherDescription").build()).build()));
    }

    @Test
    public void whenGettingAllAuctions_AllAuctionsReturned() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/auctionItems"))
                .andExpect(status().isOk()).andReturn();

        GetAuctionsResponse responseAuction = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), GetAuctionsResponse.class);

        assertEquals(3, responseAuction.getItems().size());

        for(int i = 0; i < 3; i++){
            Auction auction = responseAuction.getItems().get(0);
            Optional<AuctionDatabaseEntry> databaseEntry = auctionDatabaseEntries.stream()
                    .filter(auctionDatabaseEntry -> auction.getId().equals(auctionDatabaseEntry.getId().toHexString()))
                    .findFirst();
            assertTrue(databaseEntry.isPresent());
            verifyAuctionContents(auction, databaseEntry.get());
        }
    }

    @Test
    public void whenGettingAllAuctionsAndNoAuctionsExist_ReturnEmptyList() throws Exception {
        auctionRepository.deleteAll();
        MvcResult mvcResult = mockMvc.perform(get("/auctionItems"))
                .andExpect(status().isOk()).andReturn();

        GetAuctionsResponse responseAuction = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), GetAuctionsResponse.class);

        assertEquals(0, responseAuction.getItems().size());
    }

    private void verifyAuctionContents(Auction auction, AuctionDatabaseEntry auctionDatabaseEntry) {
        assertEquals(auctionDatabaseEntry.getId().toHexString(), auction.getId());
        assertEquals(auctionDatabaseEntry.getCurrentBid(), auction.getCurrentBid());
        assertEquals(auctionDatabaseEntry.getReservePrice(), auction.getReservePrice());
        assertEquals(auctionDatabaseEntry.getBidderName(), auction.getBidderName());
        assertEquals(auctionDatabaseEntry.getItem().getItemId(), auction.getItem().getItemId());
        assertEquals(auctionDatabaseEntry.getItem().getDescription(), auction.getItem().getDescription());
    }
}
