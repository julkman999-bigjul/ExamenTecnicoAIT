package com.proyecto.examen.controller;

import com.proyecto.examen.dto.CreateDriver;
import com.proyecto.examen.model.Driver;
import com.proyecto.examen.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<Driver> createDriver(@Valid @RequestBody CreateDriver dto) {
        Driver created = driverService.createDriver(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Driver>> listActiveDrivers() {
        return ResponseEntity.ok(driverService.listActiveDrivers());
    }
}
