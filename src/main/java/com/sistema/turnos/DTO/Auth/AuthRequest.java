package com.sistema.turnos.DTO.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {

    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
