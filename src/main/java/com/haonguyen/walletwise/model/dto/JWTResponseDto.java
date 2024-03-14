package com.haonguyen.walletwise.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JWTResponseDto {
    private Long id;
    private String email;
    private String name;
    private String role;
    private TokenDto token;
}
