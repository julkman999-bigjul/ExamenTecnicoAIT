package com.proyecto.examen.service;

import com.proyecto.examen.dto.CreateDriver;
import com.proyecto.examen.model.Driver;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Driver> driverRowMapper = (rs, rowNum) -> Driver.builder()
            .id(UUID.fromString(rs.getString("id")))
            .name(rs.getString("name"))
            .licenseNumber(rs.getString("licenseNumber"))
            .active(rs.getBoolean("active"))
            .build();

    public Driver createDriver(CreateDriver dto) {
        Driver driver = Driver.builder()
                .id(UUID.randomUUID())
                .name(dto.getName())
                .licenseNumber(dto.getLicenseNumber())
                .active(true)
                .build();

        String sql = "INSERT INTO driver (id, name, licenseNumber, active) VALUES (?, ?, ?, ?)";

        try {
            jdbcTemplate.update(
                    sql,
                    driver.getId(),
                    driver.getName(),
                    driver.getLicenseNumber(),
                    driver.isActive()
            );
            return driver;
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un conductor con la licencia: " + dto.getLicenseNumber()
            );
        }
    }

    public List<Driver> listActiveDrivers() {
        String sql = "SELECT id, name, licenseNumber, active FROM driver WHERE active = true ORDER BY name ASC";
        return jdbcTemplate.query(sql, driverRowMapper);
    }
}