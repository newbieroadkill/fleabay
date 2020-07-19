package com.ally.fleabay.bid;

import com.ally.fleabay.events.OutbidEvent;
import com.ally.fleabay.events.publisher.CustomEventPublisher;
import com.ally.fleabay.models.bid.BidDatabaseEntry;
import com.ally.fleabay.models.bid.BidRequest;
import com.ally.fleabay.models.Item;
import com.ally.fleabay.models.auction.AuctionDatabaseEntry;
import com.ally.fleabay.repositories.AuctionRepository;
import com.ally.fleabay.repositories.BidRepostiory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureDataMongo
@AutoConfigureMockMvc
public class SuccessfulBidTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepostiory bidRepostiory;

    @MockBean
    private CustomEventPublisher customEventPublisher;

    AuctionDatabaseEntry auctionDatabaseEntry;

    @BeforeEach
    public void setup(){
        auctionRepository.deleteAll();
        bidRepostiory.deleteAll();
        auctionDatabaseEntry = auctionRepository.save(AuctionDatabaseEntry.builder()
                .reservePrice(BigDecimal.TEN.setScale(2, RoundingMode.DOWN))
                .currentBid(BigDecimal.ZERO.setScale(2, RoundingMode.DOWN))
                .item(Item.builder().itemId("id").description("description").build()).build());
    }
    
    @Test
    public void whenNoOtherBidsHaveBeenPlaced_SetNewBidWhenReceived() throws Exception {
        BidRequest bidRequest = BidRequest.builder().auctionItemId(auctionDatabaseEntry.getId().toHexString())
                .maxAutoBidAmount(new BigDecimal("1.00"))
                .bidderName("Bob Al'Hashib").build();

        mockMvc.perform(post("/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(bidRequest))
        ).andExpect(status().isOk()).andReturn();

        AuctionDatabaseEntry updatedDatabaseEntry = auctionRepository.findById(auctionDatabaseEntry.getId()).get();

        assertEquals(new BigDecimal("1.00"), updatedDatabaseEntry.getCurrentBid());
        assertEquals(new BigDecimal("1.00"), updatedDatabaseEntry.getMaximumBid());
        assertEquals("Bob Al'Hashib", updatedDatabaseEntry.getBidderName());
    }

    @Test
    public void whenNewValidBidComesIn_LogBid() throws Exception {
        BidRequest bidRequest = BidRequest.builder().auctionItemId(auctionDatabaseEntry.getId().toHexString())
                .maxAutoBidAmount(new BigDecimal("1.00"))
                .bidderName("Bob Al'Hashib").build();

        mockMvc.perform(post("/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(bidRequest))
        ).andExpect(status().isOk()).andReturn();

        List<BidDatabaseEntry> logs = bidRepostiory.findAll();

        assertEquals(1, logs.size());
        assertEquals(auctionDatabaseEntry.getId().toHexString(), logs.get(0).getAuctionItemId());
        assertEquals(new BigDecimal("1.00"), logs.get(0).getMaxAutoBidAmount());
        assertEquals("Bob Al'Hashib", logs.get(0).getBidderName());
    }

    @Test
    public void whenNoOtherBidsHaveBeenPlacedAndMaxBidGreaterThanReserve_SetNewBidToReserveValue() throws Exception {
        BidRequest bidRequest = BidRequest.builder().auctionItemId(auctionDatabaseEntry.getId().toHexString())
                .maxAutoBidAmount(new BigDecimal("20.00"))
                .bidderName("Chuck Al'Hashib").build();

        mockMvc.perform(post("/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(bidRequest))
        ).andExpect(status().isOk()).andReturn();

        AuctionDatabaseEntry updatedDatabaseEntry = auctionRepository.findById(auctionDatabaseEntry.getId()).get();

        assertEquals(new BigDecimal("10.00"), updatedDatabaseEntry.getCurrentBid());
        assertEquals(new BigDecimal("20.00"), updatedDatabaseEntry.getMaximumBid());
        assertEquals("Chuck Al'Hashib", updatedDatabaseEntry.getBidderName());
    }

    @Test
    public void whenOtherBidsHaveBeenPlacedAndMaxBidGreaterThanCurrentMaxBid_SetCurrentBidToOneDollarMore() throws Exception {
        auctionDatabaseEntry = auctionRepository.save(AuctionDatabaseEntry.builder()
                .reservePrice(BigDecimal.TEN.setScale(2, RoundingMode.DOWN))
                .currentBid(new BigDecimal("5.00").setScale(2, RoundingMode.DOWN))
                .maximumBid(new BigDecimal("15.00").setScale(2, RoundingMode.DOWN))
                .bidderName("Chuck Al'Hashib")
                .item(Item.builder().itemId("id").description("description").build()).build());

        BidRequest bidRequest = BidRequest.builder().auctionItemId(auctionDatabaseEntry.getId().toHexString())
                .maxAutoBidAmount(new BigDecimal("20.00"))
                .bidderName("Bob Al'Hashib").build();

        mockMvc.perform(post("/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(bidRequest))
        ).andExpect(status().isOk()).andReturn();

        AuctionDatabaseEntry updatedDatabaseEntry = auctionRepository.findById(auctionDatabaseEntry.getId()).get();

        assertEquals(new BigDecimal("16.00"), updatedDatabaseEntry.getCurrentBid());
        assertEquals(new BigDecimal("20.00"), updatedDatabaseEntry.getMaximumBid());
        assertEquals("Bob Al'Hashib", updatedDatabaseEntry.getBidderName());
    }

    @Test
    public void whenOtherBidsHaveBeenPlacedAndMaxBidGreaterThanCurrentMaxBid_SendOutbidEvent() throws Exception {
        auctionDatabaseEntry = auctionRepository.save(AuctionDatabaseEntry.builder()
                .reservePrice(BigDecimal.TEN.setScale(2, RoundingMode.DOWN))
                .currentBid(new BigDecimal("5.00").setScale(2, RoundingMode.DOWN))
                .maximumBid(new BigDecimal("15.00").setScale(2, RoundingMode.DOWN))
                .bidderName("Chuck Al'Hashib")
                .item(Item.builder().itemId("id").description("description").build()).build());

        BidRequest bidRequest = BidRequest.builder().auctionItemId(auctionDatabaseEntry.getId().toHexString())
                .maxAutoBidAmount(new BigDecimal("20.00"))
                .bidderName("Bob Al'Hashib").build();

        ArgumentCaptor<OutbidEvent> argumentCaptor = ArgumentCaptor.forClass(OutbidEvent.class);

        mockMvc.perform(post("/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(bidRequest))
        ).andExpect(status().isOk()).andReturn();

        verify(customEventPublisher).publishOutbidEvent(argumentCaptor.capture());
        OutbidEvent outbidEvent = argumentCaptor.getValue();
        assertEquals(auctionDatabaseEntry.getId().toHexString(), outbidEvent.getAuctionItemId());
        assertEquals("Chuck Al'Hashib", outbidEvent.getBidderName());
    }

    @Test
    public void whenNewBidIsLessThanOneDollarGreaterThanCurrenMaxBid_SetCurrentBidToMaxBid() throws Exception {
        auctionDatabaseEntry = auctionRepository.save(AuctionDatabaseEntry.builder()
                .reservePrice(BigDecimal.TEN.setScale(2, RoundingMode.DOWN))
                .currentBid(new BigDecimal("5.00").setScale(2, RoundingMode.DOWN))
                .maximumBid(new BigDecimal("15.00").setScale(2, RoundingMode.DOWN))
                .bidderName("Chuck Al'Hashib")
                .item(Item.builder().itemId("id").description("description").build()).build());

        BidRequest bidRequest = BidRequest.builder().auctionItemId(auctionDatabaseEntry.getId().toHexString())
                .maxAutoBidAmount(new BigDecimal("15.01"))
                .bidderName("Bob Al'Hashib").build();

        mockMvc.perform(post("/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(bidRequest))
        ).andExpect(status().isOk()).andReturn();

        AuctionDatabaseEntry updatedDatabaseEntry = auctionRepository.findById(auctionDatabaseEntry.getId()).get();

        assertEquals(new BigDecimal("15.01"), updatedDatabaseEntry.getCurrentBid());
        assertEquals(new BigDecimal("15.01"), updatedDatabaseEntry.getMaximumBid());
        assertEquals("Bob Al'Hashib", updatedDatabaseEntry.getBidderName());
    }

    @Test
    public void whenRequestComesInWithSameMaxBidAsCurrentMaxBid_UpdateCurrentBidButNotBidderName() throws Exception {
        auctionDatabaseEntry = auctionRepository.save(AuctionDatabaseEntry.builder()
                .reservePrice(BigDecimal.TEN.setScale(2, RoundingMode.DOWN))
                .currentBid(new BigDecimal("5.00").setScale(2, RoundingMode.DOWN))
                .maximumBid(new BigDecimal("15.00").setScale(2, RoundingMode.DOWN))
                .bidderName("Chuck Al'Hashib")
                .item(Item.builder().itemId("id").description("description").build()).build());

        BidRequest bidRequest = BidRequest.builder().auctionItemId(auctionDatabaseEntry.getId().toHexString())
                .maxAutoBidAmount(new BigDecimal("15.00"))
                .bidderName("Bob Al'Hashib").build();

        mockMvc.perform(post("/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(bidRequest))
        ).andExpect(status().isOk()).andReturn();

        AuctionDatabaseEntry updatedDatabaseEntry = auctionRepository.findById(auctionDatabaseEntry.getId()).get();

        assertEquals(new BigDecimal("15.00"), updatedDatabaseEntry.getCurrentBid());
        assertEquals(new BigDecimal("15.00"), updatedDatabaseEntry.getMaximumBid());
        assertEquals("Chuck Al'Hashib", updatedDatabaseEntry.getBidderName());
        
    }
}
