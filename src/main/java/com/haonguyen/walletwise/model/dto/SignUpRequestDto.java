package com.haonguyen.walletwise.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignUpRequestDto {
    @NotEmpty(message = "Email is required")
    @Email(message = "Email invalid format")
    private String email;

    @NotEmpty(message = "Name is required")
    @Size(min = 3, max = 20)
    private String name;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20)
    private String password;
}
