package com.jyoxin.smartshop.controller;

import com.jyoxin.smartshop.core.annotation.Authenticated;
import com.jyoxin.smartshop.core.annotation.HasRole;
import com.jyoxin.smartshop.dto.request.CreatePaymentRequest;
import com.jyoxin.smartshop.dto.response.PaymentDTO;
import com.jyoxin.smartshop.entity.enums.PaymentStatus;
import com.jyoxin.smartshop.entity.enums.Role;
import com.jyoxin.smartshop.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/orders/{orderId}/payments")
    @Authenticated
    @HasRole(Role.ADMIN)
    public ResponseEntity<PaymentDTO> recordPayment(
            @PathVariable Long orderId,
            @Valid @RequestBody CreatePaymentRequest request) {
        request.setOrderId(orderId);
        return new ResponseEntity<>(paymentService.recordPayment(request), HttpStatus.CREATED);
    }

    @GetMapping("/orders/{orderId}/payments")
    @Authenticated
    @HasRole(Role.ADMIN)
    public ResponseEntity<List<PaymentDTO>> getPaymentsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId));
    }

    @PutMapping("/payments/{id}/status")
    @Authenticated
    @HasRole(Role.ADMIN)
    public ResponseEntity<PaymentDTO> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(id, status));
    }
}
