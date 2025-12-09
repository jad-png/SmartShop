package com.jyoxin.smartshop.service.impl;

import com.jyoxin.smartshop.core.exception.BusinessRuleException;
import com.jyoxin.smartshop.core.exception.ResourceNotFoundException;
import com.jyoxin.smartshop.core.validation.PaymentValidator;
import com.jyoxin.smartshop.dto.request.CreatePaymentRequest;
import com.jyoxin.smartshop.dto.response.PaymentDTO;
import com.jyoxin.smartshop.entity.Order;
import com.jyoxin.smartshop.entity.Payment;
import com.jyoxin.smartshop.entity.enums.OrderStatus;
import com.jyoxin.smartshop.entity.enums.PaymentMethod;
import com.jyoxin.smartshop.entity.enums.PaymentStatus;
import com.jyoxin.smartshop.mapper.PaymentMapper;
import com.jyoxin.smartshop.repository.OrderRepository;
import com.jyoxin.smartshop.repository.PaymentRepository;
import com.jyoxin.smartshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentValidator paymentValidator;

    @Override
    @Transactional
    public PaymentDTO recordPayment(CreatePaymentRequest request) {
        paymentValidator.validate(request);

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException(
                    "Payments can only be added to PENDING orders. Current status: " + order.getStatus(),
                    "INVALID_ORDER_STATUS_FOR_PAYMENT");
        }

        if (request.getAmount().compareTo(order.getRemainingAmount()) > 0) {
            throw new BusinessRuleException(
                    "Payment amount (" + request.getAmount() + ") exceeds remaining amount ("
                            + order.getRemainingAmount() + ")",
                    "PAYMENT_EXCEEDS_REMAINING");
        }

        int paymentNumber = paymentRepository.countByOrderId(order.getId()) + 1;

        PaymentStatus initialStatus = PaymentStatus.PENDING;
        LocalDateTime encashmentDate = null;
        if (request.getMethod() == PaymentMethod.CASH) {
            initialStatus = PaymentStatus.COLLECTED;
            encashmentDate = LocalDateTime.now();
        }

        Payment payment = paymentMapper.toEntity(request);
        payment.setOrder(order);
        payment.setPaymentNumber(paymentNumber);
        payment.setStatus(initialStatus);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setEncashmentDate(encashmentDate);

        payment = paymentRepository.save(payment);

        if (payment.getStatus() == PaymentStatus.COLLECTED) {
            order.setTotalPaid(order.getTotalPaid().add(request.getAmount()));
            order.setRemainingAmount(order.getRemainingAmount().subtract(request.getAmount()));
            orderRepository.save(order);
        }

        return paymentMapper.toResponse(payment);
    }

    @Override
    public List<PaymentDTO> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentDTO updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() == status) {
            return paymentMapper.toResponse(payment);
        }

        if (payment.getMethod() == PaymentMethod.CASH) {
            throw new BusinessRuleException("Cash payments cannot have their status changed", "INVALID_STATUS_CHANGE");
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING payments can be processed", "INVALID_STATUS_CHANGE");
        }

        Order order = payment.getOrder();

        if (status == PaymentStatus.COLLECTED) {
            payment.setStatus(PaymentStatus.COLLECTED);
            payment.setEncashmentDate(LocalDateTime.now());

            order.setTotalPaid(order.getTotalPaid().add(payment.getAmount()));
            order.setRemainingAmount(order.getRemainingAmount().subtract(payment.getAmount()));

        } else if (status == PaymentStatus.REJECTED) {
            payment.setStatus(PaymentStatus.REJECTED);
        }

        orderRepository.save(order);
        return paymentMapper.toResponse(paymentRepository.save(payment));
    }
}
