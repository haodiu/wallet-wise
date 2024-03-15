package com.haonguyen.walletwise.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalAmountDto {
    @JsonProperty("category_id")
    private Long categoryId;
    private Long amount;
}
