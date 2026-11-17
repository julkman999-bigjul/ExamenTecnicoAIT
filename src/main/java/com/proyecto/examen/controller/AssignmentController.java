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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Assignment> assignDriver(
            @RequestParam("orderId") UUID orderId,
            @RequestParam("driverId") UUID driverId,
            @RequestParam(value = "pdf", required = false) MultipartFile pdf,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        Assignment created = assignmentService.assignDriver(orderId, driverId, pdf, image);
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

    //Listado de ordenes asignadas
    @GetMapping
    public ResponseEntity<List<Assignment>> listAssignments() {
        return ResponseEntity.ok(assignmentService.listAssignments());
    }

}