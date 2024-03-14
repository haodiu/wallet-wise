package com.haonguyen.walletwise.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordDto {
    private String token;

    private String email;

    @JsonProperty("new_password")
    private String newPassword;
}