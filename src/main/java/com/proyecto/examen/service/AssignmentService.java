package com.proyecto.examen.service;

import java.util.List;
import com.proyecto.examen.model.Assignment;
import com.proyecto.examen.model.Order;
import com.proyecto.examen.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final JdbcTemplate jdbcTemplate;
    private final OrderService orderService;

    public Assignment assignDriver(UUID orderId, UUID driverId, MultipartFile pdf, MultipartFile image) {
        // 1. Verificar que el conductor exista y esté activo
        Boolean isActive;
        try {
            isActive = jdbcTemplate.queryForObject(
                    "SELECT active FROM driver WHERE id = ?",
                    Boolean.class,
                    driverId
            );
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conductor no encontrado");
        }

        if (isActive == null || !isActive) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El conductor no está activo");
        }

        Order order = orderService.getOrderById(orderId);
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se puede asignar una orden en estado CREATED (estado actual: " + order.getStatus() + ")"
            );
        }

        UUID assignmentId = UUID.randomUUID();
        String pdfPath = null;
        String imagePath = null;

        if (pdf != null && !pdf.isEmpty()) {
            String pdfName = pdf.getOriginalFilename();
            if (pdfName == null || !pdfName.toLowerCase().endsWith(".pdf")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permiten archivos en formato .pdf");
            }
            pdfPath = saveFileToDisk(pdf, assignmentId + "_" + pdfName);
        }

        // 4. Validar y guardar Imagen si viene adjunta
        if (image != null && !image.isEmpty()) {
            String imgName = image.getOriginalFilename();
            String lowerName = imgName != null ? imgName.toLowerCase() : "";
            if (!lowerName.endsWith(".png") && !lowerName.endsWith(".jpg") && !lowerName.endsWith(".jpeg")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permiten imágenes .png o .jpg");
            }
            imagePath = saveFileToDisk(image, assignmentId + "_" + imgName);
        }

        Assignment assignment = Assignment.builder()
                .id(assignmentId)
                .orderId(orderId)
                .driverId(driverId)
                .pdf(pdfPath)
                .image(imagePath)
                .build();

        String sql = "INSERT INTO assignment (id, order_id, driver_id, pdf, image) VALUES (?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.update(
                    sql,
                    assignment.getId(),
                    assignment.getOrderId(),
                    assignment.getDriverId(),
                    assignment.getPdf(),
                    assignment.getImage()
            );
            return assignment;
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta orden ya tiene un conductor asignado");
        }
    }


    //ARCHIVOS
    public String uploadPdf(UUID assignmentId, MultipartFile file) {
        validateAssignmentExists(assignmentId);

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo PDF es obligatorio");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permiten archivos en formato .pdf");
        }

        String savedPath = saveFileToDisk(file, assignmentId + "_" + originalName);
        jdbcTemplate.update("UPDATE assignment SET pdf = ? WHERE id = ?", savedPath, assignmentId);
        return savedPath;
    }

    //IMAGENES
    public String uploadImage(UUID assignmentId, MultipartFile file) {
        validateAssignmentExists(assignmentId);

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La imagen es obligatoria");
        }

        String originalName = file.getOriginalFilename();
        String lowerName = originalName != null ? originalName.toLowerCase() : "";
        if (!lowerName.endsWith(".png") && !lowerName.endsWith(".jpg") && !lowerName.endsWith(".jpeg")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permiten imágenes .png o .jpg");
        }

        String savedPath = saveFileToDisk(file, assignmentId + "_" + originalName);
        jdbcTemplate.update("UPDATE assignment SET image = ? WHERE id = ?", savedPath, assignmentId);
        return savedPath;
    }

    private void validateAssignmentExists(UUID assignmentId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM assignment WHERE id = ?",
                Integer.class,
                assignmentId
        );
        if (count == null || count == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignación no encontrada");
        }
    }

    private String saveFileToDisk(MultipartFile file, String fileName) {
        try {
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path destination = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return destination.toString();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el archivo");
        }
    }

        public List<Assignment> listAssignments() {
        String sql = "SELECT id, order_id, driver_id, pdf, image FROM assignment";
        return jdbcTemplate.query(sql, (rs, rowNum) -> Assignment.builder()
                .id(UUID.fromString(rs.getString("id")))
                .orderId(UUID.fromString(rs.getString("order_id")))
                .driverId(UUID.fromString(rs.getString("driver_id")))
                .pdf(rs.getString("pdf"))
                .image(rs.getString("image"))
                .build());
    }

}