package com.jyoxin.smartshop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;
}
