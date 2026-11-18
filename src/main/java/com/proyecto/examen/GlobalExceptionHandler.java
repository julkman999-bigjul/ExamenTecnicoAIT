package com.proyecto.examen;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //Esepciones
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        log.warn("Error de negocio ({}): {}", ex.getStatus().value(), ex.getReason());
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().withNano(0).toString());
        body.put("status", ex.getStatus().value());
        body.put("error", ex.getReason());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    //Validaciones
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        log.warn("Error de validación en petición: {}", fieldErrors);
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().withNano(0).toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Error de validación en los datos enviados");
        body.put("details", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }
}
