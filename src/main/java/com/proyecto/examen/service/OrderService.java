package com.proyecto.examen.service;

import com.proyecto.examen.dto.CreateOrder;
import com.proyecto.examen.dto.UpdateStatus;
import com.proyecto.examen.model.Order;
import com.proyecto.examen.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Order> orderRowMapper = (rs, rowNum) -> Order.builder()
            .id(UUID.fromString(rs.getString("id")))
            .status(OrderStatus.valueOf(rs.getString("status")))
            .origin(rs.getString("origin"))
            .destination(rs.getString("destination"))
            .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
            .updatedAt(rs.getTimestamp("updatedAt").toLocalDateTime())
            .build();

    // 1. Crear una nueva orden
    public Order createOrder(CreateOrder dto) {
        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .status(OrderStatus.CREATED)
                .origin(dto.getOrigin())
                .destination(dto.getDestination())
                .createdAt(now)
                .updatedAt(now)
                .build();

        String sql = "INSERT INTO \"order\" (id, status, origin, destination, createdAt, updatedAt) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                order.getId(),
                order.getStatus().name(),
                order.getOrigin(),
                order.getDestination(),
                Timestamp.valueOf(order.getCreatedAt()),
                Timestamp.valueOf(order.getUpdatedAt())
        );

        return order;
    }

    // 2. Consultar orden por ID
    public Order getOrderById(UUID id) {
        String sql = "SELECT id, status, origin, destination, createdAt, updatedAt FROM \"order\" WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, orderRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada con ID: " + id);
        }
    }

    // 3. Cambiar el estado de la orden (validando flujo válido)
    public Order updateOrderStatus(UUID id, UpdateStatus dto) {
        Order currentOrder = getOrderById(id);
        OrderStatus actualStatus = currentOrder.getStatus();
        OrderStatus nuevoStatus = dto.getStatus();

        if (!isValidTransition(actualStatus, nuevoStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cambio de estado incorrecto: " + actualStatus + " a " + nuevoStatus
            );
        }

        LocalDateTime now = LocalDateTime.now();
        String sql = "UPDATE \"order\" SET status = ?, updatedAt = ? WHERE id = ?";
        jdbcTemplate.update(sql, nuevoStatus.name(), Timestamp.valueOf(now), id);

        currentOrder.setStatus(nuevoStatus);
        currentOrder.setUpdatedAt(now);
        return currentOrder;
    }

    // 4. Listar órdenes con filtros opcionales (status, fecha, origen o destino)
    public List<Order> listOrders(OrderStatus status, LocalDate date, String origin, String destination) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, status, origin, destination, createdAt, updatedAt FROM \"order\" WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status.name());
        }
        if (date != null) {
            sql.append(" AND DATE(createdAt) = ?");
            params.add(java.sql.Date.valueOf(date));
        }
        if (origin != null && !origin.isBlank()) {
            sql.append(" AND origin ILIKE ?");
            params.add("%" + origin.trim() + "%");
        }
        if (destination != null && !destination.isBlank()) {
            sql.append(" AND destination ILIKE ?");
            params.add("%" + destination.trim() + "%");
        }

        sql.append(" ORDER BY createdAt DESC");

        return jdbcTemplate.query(sql.toString(), orderRowMapper, params.toArray());
    }

    // Reglas de transición válida de estados
    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        switch (current) {
            case CREATED:
                return next == OrderStatus.IN_TRANSIT || next == OrderStatus.CANCELLED;
            case IN_TRANSIT:
                return next == OrderStatus.DELIVERED || next == OrderStatus.CANCELLED;
            case DELIVERED:
            case CANCELLED:
            default:
                return false;
        }
    }
}
