package com.jyoxin.smartshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "promo_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode extends BaseEntity {

    @NotBlank(message = "Code is required")
    @Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Code must match format PROMO-XXXX where X is alphanumeric uppercase")
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Min(value = 0, message = "Percentage cannot be negative")
    @Max(value = 100, message = "Percentage cannot be greater than 100")
    @Column(nullable = false)
    private BigDecimal percentage;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Min(value = 0, message = "Max usage cannot be negative")
    @Column(name = "max_usage", nullable = false)
    @Builder.Default
    private int maxUsage = 1;

    @Column(name = "current_usage", nullable = false)
    @Builder.Default
    private int currentUsage = 0;

    /**
     * Check if promo code still has remaining uses
     */
    public boolean hasRemainingUsage() {
        return currentUsage < maxUsage;
    }

    /**
     * Get remaining usage count
     */
    public int getRemainingUsage() {
        return maxUsage - currentUsage;
    }
}
