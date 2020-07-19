package com.ally.fleabay.bid;

import com.ally.fleabay.models.bid.BidRequest;
import com.ally.fleabay.models.Item;
import com.ally.fleabay.models.auction.AuctionDatabaseEntry;
import com.ally.fleabay.repositories.AuctionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureDataMongo
@AutoConfigureMockMvc
public class BidTransactionTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuctionRepository auctionRepository;

    AuctionDatabaseEntry auctionDatabaseEntry;

    @BeforeEach
    public void setup(){
        auctionDatabaseEntry = AuctionDatabaseEntry.builder()
                .id(ObjectId.get())
                .reservePrice(BigDecimal.TEN.setScale(2, RoundingMode.DOWN))
                .currentBid(BigDecimal.ZERO.setScale(2, RoundingMode.DOWN))
                .item(Item.builder().itemId("id").description("description").build()).build();

        Mockito.when(auctionRepository.findById(Mockito.any())).thenReturn(Optional.of(auctionDatabaseEntry));
        Mockito.when(auctionRepository.save(Mockito.any())).thenThrow(new OptimisticLockingFailureException("version conflict"));
    }

    @Test
    public void whenUnableToPersistBidResultsAfterRetries_Return503() throws Exception {
        BidRequest bidRequest = BidRequest.builder().auctionItemId(auctionDatabaseEntry.getId().toHexString())
                .maxAutoBidAmount(new BigDecimal("1.00"))
                .bidderName("Bob Al'Hashib").build();

        mockMvc.perform(post("/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(bidRequest))
        ).andExpect(status().isServiceUnavailable()).andReturn();
    }
}




