package com.proyecto.examen.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "El usuario es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
