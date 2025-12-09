package com.jyoxin.smartshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientStats {

    @Column(name = "total_orders")
    @Builder.Default
    private int totalOrders = 0;

    @Column(name = "total_spent", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(name = "first_order_date")
    private LocalDate firstOrderDate;

    @Column(name = "last_order_date")
    private LocalDate lastOrderDate;

    /**
     * Update stats after an order is confirmed.
     * Note: This only updates numbers. Tier calculation is handled by LoyaltyService.
     * @param orderAmount the total TTC amount of the confirmed order
     */
    public void recordOrder(BigDecimal orderAmount) {
        this.totalOrders++;
        this.totalSpent = this.totalSpent.add(orderAmount);
        this.lastOrderDate = LocalDate.now();

        if (this.firstOrderDate == null) {
            this.firstOrderDate = LocalDate.now();
        }
    }

    /**
     * Create default empty stats
     */
    public static ClientStats empty() {
        return ClientStats.builder()
                .totalOrders(0)
                .totalSpent(BigDecimal.ZERO)
                .firstOrderDate(null)
                .lastOrderDate(null)
                .build();
    }
}