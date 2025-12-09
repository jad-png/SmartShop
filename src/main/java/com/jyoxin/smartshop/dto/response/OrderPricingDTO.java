package com.jyoxin.smartshop.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderPricingDTO {
    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal loyaltyDiscount;
    private BigDecimal promoDiscount;
    private BigDecimal tvaAmount;
    private BigDecimal totalTtc;
    private List<String> discountSources;
    private String appliedPromoCode;
}
