package com.haonguyen.walletwise.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInRequestDto {
    @NotEmpty(message = "Email is required")
    @Email(message = "Email invalid format")
    @Size(max = 50)
    private String email;

    @NotEmpty(message = "Password is required")
    @Size(min = 6, max = 50)
    private String password;
}
