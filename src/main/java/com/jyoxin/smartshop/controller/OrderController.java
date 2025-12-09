package com.jyoxin.smartshop.controller;

import com.jyoxin.smartshop.core.annotation.Authenticated;
import com.jyoxin.smartshop.core.annotation.HasRole;
import com.jyoxin.smartshop.core.exception.UnauthorizedException;
import com.jyoxin.smartshop.dto.request.CreateOrderRequest;
import com.jyoxin.smartshop.dto.response.OrderDTO;
import com.jyoxin.smartshop.entity.enums.Role;
import com.jyoxin.smartshop.service.AuthService;
import com.jyoxin.smartshop.service.ClientService;
import com.jyoxin.smartshop.service.OrderService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;
    private final ClientService clientService;

    @PostMapping
    @Authenticated
    @HasRole(Role.ADMIN)
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return new ResponseEntity<>(orderService.createOrder(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Authenticated
    @HasRole(Role.ADMIN)
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    @Authenticated
    @HasRole(Role.ADMIN)
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(required = false) Long clientId,
            Pageable pageable) {
        if (clientId != null) {
            return ResponseEntity.ok(orderService.getOrdersByClientId(clientId, pageable));
        }
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @PutMapping("/{id}/confirm")
    @Authenticated
    @HasRole(Role.ADMIN)
    public ResponseEntity<OrderDTO> confirmOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.confirmOrder(id));
    }

    @PutMapping("/{id}/cancel")
    @Authenticated
    @HasRole(Role.ADMIN)
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @GetMapping("/my-orders")
    @Authenticated
    @HasRole(Role.CLIENT)
    public ResponseEntity<Page<OrderDTO>> getMyOrders(Pageable pageable, HttpSession session) {
        var currentUser = authService.getCurrentUser(session);
        var client = clientService.getClientByUserId(currentUser.getId());
        return ResponseEntity.ok(orderService.getOrdersByClientId(client.getId(), pageable));
    }

    @GetMapping("/my-orders/{id}")
    @Authenticated
    @HasRole(Role.CLIENT)
    public ResponseEntity<OrderDTO> getMyOrderById(@PathVariable Long id, HttpSession session) {
        var currentUser = authService.getCurrentUser(session);
        var client = clientService.getClientByUserId(currentUser.getId());
        OrderDTO order = orderService.getOrderById(id);

        if (!order.getClientId().equals(client.getId())) {
            throw new UnauthorizedException("You can only view your own orders");
        }

        return ResponseEntity.ok(order);
    }
}
