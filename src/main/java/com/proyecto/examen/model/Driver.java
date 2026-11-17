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
public class Driver {
    private UUID id;
    private String name;
    private String licenseNumber;
    private boolean active;
}
