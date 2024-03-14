package com.haonguyen.walletwise.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // Ignore null fields
public class TransactionDto {
    private Long id;
    private String name;
    private Long amount;
    private String note;
    private Date date;
    @JsonProperty("category_id")
    private Long categoryId;
}
