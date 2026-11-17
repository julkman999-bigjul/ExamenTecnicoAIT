package com.proyecto.examen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    private UUID id;
    private UUID orderId;
    private UUID driverId;
    private String pdf;
    private String image;
}
