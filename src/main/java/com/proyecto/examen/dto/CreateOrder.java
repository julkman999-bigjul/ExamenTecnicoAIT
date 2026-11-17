package com.proyecto.examen.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class CreateOrder {
    @NotBlank(message = "El origen es obligatorio")
    private String origin;

    @NotBlank(message = "El destino es obligatorio")
    private String destination;
}
