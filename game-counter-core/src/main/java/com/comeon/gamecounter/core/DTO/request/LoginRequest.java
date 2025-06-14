package com.comeon.gamecounter.core.DTO.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {
    @NotNull
    private Long playerId;

    @NotBlank
    private String password;
}
