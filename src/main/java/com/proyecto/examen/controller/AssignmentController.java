package com.proyecto.examen.controller;

import com.proyecto.examen.dto.CreateAssignment;
import com.proyecto.examen.model.Assignment;
import com.proyecto.examen.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<Assignment> assignDriver(@Valid @RequestBody CreateAssignment dto) {
        Assignment created = assignmentService.assignDriver(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    // PDF
    @PostMapping(value = "/{id}/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadPdf(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        String path = assignmentService.uploadPdf(id, file);
        return ResponseEntity.ok(Map.of("message", "PDF cargado correctamente", "path", path));
    }

    // IMAGEN
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        String path = assignmentService.uploadImage(id, file);
        return ResponseEntity.ok(Map.of("message", "Imagen cargada correctamente", "path", path));
    }
}