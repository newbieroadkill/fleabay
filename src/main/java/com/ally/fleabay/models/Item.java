package com.ally.fleabay.models;

import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Item {
    @NotNull
    String itemId;

    @NotNull
    String description;
}
