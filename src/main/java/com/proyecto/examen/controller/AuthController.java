package com.proyecto.examen.controller;

import com.proyecto.examen.config.JwtUtil;
import com.proyecto.examen.dto.LoginRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    @Value("${spring.security.user.name}")
    private String validUser;

    @Value("${spring.security.user.password}")
    private String validPassword;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        if (validUser.equals(username) && validPassword.equals(password)) {
            String token = jwtUtil.generateToken(username);
            log.info("Token JWT generado para el usuario: {}", username);
            return ResponseEntity.ok(Map.of("token", token, "type", "Bearer"));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
    }

}
