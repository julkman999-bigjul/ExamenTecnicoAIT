package com.proyecto.examen.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CreateAssignment {
    @NotNull(message = "El ID de la orden es obligatorio")
    private UUID orderId;

    @NotNull(message = "El ID del conductor es obligatorio")
    private UUID driverId;
}
