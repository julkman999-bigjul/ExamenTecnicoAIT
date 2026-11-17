package com.proyecto.examen.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class CreateDriver {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El número de licencia es obligatorio")
    private String licenseNumber;
}
