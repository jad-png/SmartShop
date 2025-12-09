package com.jyoxin.smartshop.dto.response;

import com.jyoxin.smartshop.entity.enums.PaymentMethod;
import com.jyoxin.smartshop.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private int paymentNumber;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String reference;
    private String bank;
    private LocalDate dueDate;
    private LocalDateTime paymentDate;
    private LocalDateTime encashmentDate;
}
