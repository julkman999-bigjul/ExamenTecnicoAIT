package com.proyecto.examen.service;

import com.proyecto.examen.dto.CreateOrder;
import com.proyecto.examen.dto.UpdateStatus;
import com.proyecto.examen.model.Order;
import com.proyecto.examen.model.OrderStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_ShouldCreateWithStatusCreated() {
        CreateOrder dto = new CreateOrder();
        dto.setOrigin("CDMX");
        dto.setDestination("Monterrey");

        Order result = orderService.createOrder(dto);

        assertNotNull(result.getId());
        assertEquals(OrderStatus.CREATED, result.getStatus());
        assertEquals("CDMX", result.getOrigin());
        assertEquals("Monterrey", result.getDestination());
        verify(jdbcTemplate, times(1)).update(anyString(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void updateOrderStatus_ValidTransition_ShouldUpdateStatus() {
        UUID orderId = UUID.randomUUID();
        Order existing = Order.builder()
                .id(orderId)
                .status(OrderStatus.CREATED)
                .origin("CDMX")
                .destination("Guadalajara")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Order>>any(), eq(orderId)))
                .thenReturn(existing);

        UpdateStatus dto = new UpdateStatus();
        dto.setStatus(OrderStatus.IN_TRANSIT);

        Order updated = orderService.updateOrderStatus(orderId, dto);

        assertEquals(OrderStatus.IN_TRANSIT, updated.getStatus());
        verify(jdbcTemplate, times(1)).update(anyString(), eq("IN_TRANSIT"), any(), eq(orderId));
    }

    @Test
    void updateOrderStatus_InvalidTransition_ShouldThrowBadRequest() {
        UUID orderId = UUID.randomUUID();
        Order existing = Order.builder()
                .id(orderId)
                .status(OrderStatus.CREATED)
                .origin("CDMX")
                .destination("Guadalajara")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Order>>any(), eq(orderId)))
                .thenReturn(existing);

        UpdateStatus dto = new UpdateStatus();
        dto.setStatus(OrderStatus.DELIVERED);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> orderService.updateOrderStatus(orderId, dto)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        verify(jdbcTemplate, never()).update(anyString(), any(), any(), any());
    }
}
