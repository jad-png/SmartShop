package com.jyoxin.smartshop.service;

import com.jyoxin.smartshop.dto.request.CreatePaymentRequest;
import com.jyoxin.smartshop.dto.response.PaymentDTO;
import com.jyoxin.smartshop.entity.enums.PaymentStatus;

import java.util.List;

public interface PaymentService {
    PaymentDTO recordPayment(CreatePaymentRequest request);

    List<PaymentDTO> getPaymentsByOrderId(Long orderId);

    PaymentDTO updatePaymentStatus(Long paymentId, PaymentStatus status);
}
