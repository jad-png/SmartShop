package com.jyoxin.smartshop.repository;

import com.jyoxin.smartshop.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);

    int countByOrderId(Long orderId);

    Optional<Payment> findByOrderIdAndPaymentNumber(Long orderId, int paymentNumber);
}
