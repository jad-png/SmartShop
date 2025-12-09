package com.jyoxin.smartshop.dto.response;

import com.jyoxin.smartshop.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long clientId;
    private String clientName;
    private List<OrderItemDTO> items;
    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal tvaRate;
    private BigDecimal tvaAmount;
    private BigDecimal totalTtc;
    private BigDecimal totalPaid;
    private BigDecimal remainingAmount;
    private OrderStatus status;
    private List<PaymentDTO> payments;
    private LocalDateTime createdAt;
    private String promoCode;
}
