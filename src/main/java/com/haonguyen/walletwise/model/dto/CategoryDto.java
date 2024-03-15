package com.haonguyen.walletwise.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.haonguyen.walletwise.model.entity.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // Ignore null fields
public class CategoryDto {
    private Long id;
    private String name;
    private List<TransactionDto> transactions;
}
