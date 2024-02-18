package com.haonguyen.walletwise.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class ApiError {
    private Integer code;
    private String message;
}
