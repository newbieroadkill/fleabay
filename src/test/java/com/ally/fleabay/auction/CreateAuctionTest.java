package com.ally.fleabay.auction;

import com.ally.fleabay.models.auction.Auction;
import com.ally.fleabay.models.auction.AuctionDatabaseEntry;
import com.ally.fleabay.models.auction.CreateAuctionRequest;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    CreateAuctionRequest createAuctionRequest;

    @BeforeEach
    public void setup(){
        createAuctionRequest = CreateAuctionRequest.builder().reservePrice(BigDecimal.TEN.setScale(2,  RoundingMode.DOWN))
                .item(Item.builder().itemId("id").description("description").build()).build();
    }

    @Test
    public void whenValidRequestComesIn_SendProperOkResponse() throws Exception {
        MvcResult result = mockMvc.perform(post("/auctionItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(createAuctionRequest))
            ).andExpect(status().isOk()).andReturn();

        Auction response = objectMapper.readValue(result.getResponse().getContentAsByteArray(), Auction.class);

        assertEquals(createAuctionRequest.getReservePrice(), response.getReservePrice());
        assertEquals(createAuctionRequest.getItem().getItemId(), response.getItem().getItemId());
        assertEquals(createAuctionRequest.getItem().getDescription(), response.getItem().getDescription());
        assertEquals(BigDecimal.ZERO.setScale(2,  RoundingMode.DOWN), response.getCurrentBid());
    }

    @Test
    public void whenValidRequestComesIn_PersistAuctionObject() throws Exception {
        CreateAuctionRequest createAuctionRequest = CreateAuctionRequest.builder().reservePrice(BigDecimal.valueOf(100.00)
                .setScale(2,  RoundingMode.DOWN))
                .item(Item.builder().itemId("otherId").description("otherDescription").build()).build();

        MvcResult result = mockMvc.perform(post("/auctionItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(createAuctionRequest))
        ).andExpect(status().isOk()).andReturn();

        Auction response = objectMapper.readValue(result.getResponse().getContentAsByteArray(), Auction.class);

        Optional<AuctionDatabaseEntry> databaseEntryOptional = auctionRepository.findById(new ObjectId(response.getId()));

        assertTrue(databaseEntryOptional.isPresent());

        AuctionDatabaseEntry databaseEntry = databaseEntryOptional.get();
        assertEquals(createAuctionRequest.getReservePrice(), databaseEntry.getReservePrice());
        assertEquals(createAuctionRequest.getItem().getItemId(), databaseEntry.getItem().getItemId());
        assertEquals(createAuctionRequest.getItem().getDescription(), databaseEntry.getItem().getDescription());
        assertEquals(BigDecimal.ZERO.setScale(2,  RoundingMode.DOWN), databaseEntry.getCurrentBid());
    }

    @Test
    public void whenValidRequestComesIn_400WhenReservePriceMissing() throws Exception {
        mockMvc.perform(post("/auctionItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAuctionRequest).replace("\"reservePrice\":10.0", ""))
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void whenValidRequestComesIn_400WhenReservePriceLessThanOneDollar() throws Exception {
        createAuctionRequest.setReservePrice(BigDecimal.valueOf(.99));
        mockMvc.perform(post("/auctionItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAuctionRequest))
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void whenValidRequestComesIn_400WhenItemIdMissing() throws Exception {
        mockMvc.perform(post("/auctionItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAuctionRequest).replace("\"itemId\":\"id\",", ""))
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void whenValidRequestComesIn_400WhenItemDescriptionMissing() throws Exception {
        mockMvc.perform(post("/auctionItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAuctionRequest).replace(",\"description\":\"description\"", ""))
        ).andExpect(status().isBadRequest()).andReturn();
    }
}
