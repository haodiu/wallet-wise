package com.haonguyen.walletwise.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TransactionDto {
    private Long id;
    private Long categoryId;
    private String name;
    private Long amount;
    private String note;
    private Date date;
}
