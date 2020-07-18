package com.ally.fleabay;

import com.ally.fleabay.controllers.AuctionController;
import com.ally.fleabay.models.Auction;
import com.ally.fleabay.models.AuctionDatabaseEntry;
import com.ally.fleabay.models.CreateAuctionRequest;
import com.ally.fleabay.models.Item;
import com.ally.fleabay.repositories.AuctionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(controllers = AuctionController.class)

@SpringBootTest
@AutoConfigureDataMongo
@AutoConfigureMockMvc
public class CreateAuctionTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuctionRepository auctionRepository;

    @BeforeEach
    public void setup(){

    }

    @Test
    public void whenValidRequestComesIn_SendProperResponse() throws Exception {
        CreateAuctionRequest createAuctionRequest = CreateAuctionRequest.builder().reservePrice(10.0)
                .item(Item.builder().itemId("id").description("description").build()).build();
        
        MvcResult result = mockMvc.perform(post("/auctionItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(createAuctionRequest))
            ).andExpect(status().isOk()).andReturn();

        Auction response = objectMapper.readValue(result.getResponse().getContentAsByteArray(), Auction.class);

        assertEquals(createAuctionRequest.getReservePrice(), response.getReservePrice());
        assertEquals(createAuctionRequest.getItem().getItemId(), response.getItem().getItemId());
        assertEquals(createAuctionRequest.getItem().getDescription(), response.getItem().getDescription());
        assertEquals(0.0d, response.getCurrentBid());
    }

    @Test
    public void whenValidRequestComesIn_PersistAuctionObject() throws Exception {
        CreateAuctionRequest createAuctionRequest = CreateAuctionRequest.builder().reservePrice(10.0)
                .item(Item.builder().itemId("id").description("description").build()).build();

        MvcResult result = mockMvc.perform(post("/auctionItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(createAuctionRequest))
        ).andExpect(status().isOk()).andReturn();

        Auction response = objectMapper.readValue(result.getResponse().getContentAsByteArray(), Auction.class);

        Optional<AuctionDatabaseEntry> databaseEntryOptional = auctionRepository.findById(new ObjectId(response.getId()));

        assertTrue(databaseEntryOptional.isPresent());

        AuctionDatabaseEntry databaseEntry = databaseEntryOptional.get();
        assertEquals(createAuctionRequest.getReservePrice(), databaseEntry.getReseverePrice());
        assertEquals(createAuctionRequest.getItem().getItemId(), databaseEntry.getItem().getItemId());
        assertEquals(createAuctionRequest.getItem().getDescription(), databaseEntry.getItem().getDescription());
        assertEquals(0.0d, databaseEntry.getCurrentBid());
    }
}
