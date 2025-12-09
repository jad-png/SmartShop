package com.jyoxin.smartshop.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePromoCodeRequest {

    @NotBlank(message = "Code is required")
    @Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Code must match format PROMO-XXXX where X is alphanumeric uppercase")
    private String code;

    @Min(value = 0, message = "Percentage cannot be negative")
    @Max(value = 100, message = "Percentage cannot be greater than 100")
    private BigDecimal percentage;

    @Min(value = 1, message = "Max usage must be at least 1")
    @Builder.Default
    private int maxUsage = 1;
}
