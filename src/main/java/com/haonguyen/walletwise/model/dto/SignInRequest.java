package com.haonguyen.walletwise.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInRequest {
    @NotBlank
    @Size(max = 50)
    private String email;
    @NotBlank
    @Size(min = 6, max = 50)
    private String password;
}
