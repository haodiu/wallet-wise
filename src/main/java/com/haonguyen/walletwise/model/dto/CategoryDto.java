package com.haonguyen.walletwise.model.dto;

import com.haonguyen.walletwise.model.entity.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryDto {
    private Long id;
    private String name;
    private List<Transaction> transactionList;
}
