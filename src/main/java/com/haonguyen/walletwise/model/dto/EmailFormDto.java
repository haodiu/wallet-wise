package com.haonguyen.walletwise.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailFormDto {
    //add validation
   private String email;
}
