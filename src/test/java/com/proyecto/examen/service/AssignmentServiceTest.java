package com.proyecto.examen.service;

import com.proyecto.examen.model.Assignment;
import com.proyecto.examen.model.Order;
import com.proyecto.examen.model.OrderStatus;
import com.proyecto.examen.service.AssignmentService;
import com.proyecto.examen.service.OrderService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private AssignmentService assignmentService;

    @Test
    void assignDriver_ActiveDriverAndCreatedOrder_ShouldSucceed() {
        UUID orderId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(driverId)))
                .thenReturn(true);
        when(orderService.getOrderById(orderId))
                .thenReturn(Order.builder().id(orderId).status(OrderStatus.CREATED).build());

        Assignment result = assignmentService.assignDriver(orderId, driverId, null, null);

        assertNotNull(result.getId());
        assertEquals(orderId, result.getOrderId());
        assertEquals(driverId, result.getDriverId());
        verify(jdbcTemplate, times(1)).update(anyString(), any(), eq(orderId), eq(driverId), isNull(), isNull());
    }

    @Test
    void assignDriver_InactiveDriver_ShouldThrowBadRequest() {
        UUID orderId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(driverId)))
                .thenReturn(false);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.assignDriver(orderId, driverId, null, null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        verifyNoInteractions(orderService);
    }

    @Test
    void assignDriver_OrderNotCreated_ShouldThrowBadRequest() {
        UUID orderId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(driverId)))
                .thenReturn(true);
        when(orderService.getOrderById(orderId))
                .thenReturn(Order.builder().id(orderId).status(OrderStatus.IN_TRANSIT).build());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.assignDriver(orderId, driverId, null, null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        verify(jdbcTemplate, never()).update(anyString(), any(), any(), any(), any(), any());
    }
}
