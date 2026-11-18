package com.proyecto.examen.service;

import com.proyecto.examen.dto.CreateDriver;
import com.proyecto.examen.model.Driver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private DriverService driverService;

    @Test
    void createDriver_ShouldCreateActiveDriver() {
        CreateDriver dto = new CreateDriver();
        dto.setName("Carlos Perez");
        dto.setLicenseNumber("LIC-999");

        Driver result = driverService.createDriver(dto);

        assertNotNull(result.getId());
        assertEquals("Carlos Perez", result.getName());
        assertEquals("LIC-999", result.getLicenseNumber());
        assertTrue(result.isActive());
        verify(jdbcTemplate, times(1)).update(anyString(), any(), eq("Carlos Perez"), eq("LIC-999"), eq(true));
    }

    @Test
    void createDriver_DuplicateLicense_ShouldThrowConflict() {
        CreateDriver dto = new CreateDriver();
        dto.setName("Carlos Perez");
        dto.setLicenseNumber("LIC-999");

        when(jdbcTemplate.update(anyString(), any(), any(), any(), any()))
                .thenThrow(new DuplicateKeyException("Duplicate key"));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> driverService.createDriver(dto)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void listActiveDrivers_ShouldReturnList() {
        Driver activeDriver = Driver.builder()
                .id(UUID.randomUUID())
                .name("Ana Lopez")
                .licenseNumber("LIC-100")
                .active(true)
                .build();

        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<Driver>>any()))
                .thenReturn(List.of(activeDriver));

        List<Driver> result = driverService.listActiveDrivers();

        assertEquals(1, result.size());
        assertEquals("Ana Lopez", result.get(0).getName());
    }
}
