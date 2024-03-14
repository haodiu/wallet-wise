package com.haonguyen.walletwise.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailDto {
    private Long id;
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    private String name;
    private String role;
    private String password;
}
