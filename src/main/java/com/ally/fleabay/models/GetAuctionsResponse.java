package com.ally.fleabay.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetAuctionsResponse {
    List<Auction> items = new ArrayList<>();
}
