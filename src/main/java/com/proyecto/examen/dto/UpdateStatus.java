package com.proyecto.examen.dto;

import com.proyecto.examen.model.OrderStatus;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class UpdateStatus {
    @NotNull(message = "El nuevo estado es obligatorio")
    private OrderStatus status;
}
