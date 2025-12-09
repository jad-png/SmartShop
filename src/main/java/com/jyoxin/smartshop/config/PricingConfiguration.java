package com.jyoxin.smartshop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "smartshop.pricing")
@Data
public class PricingConfiguration {
    private BigDecimal tvaRate = new BigDecimal("0.20");
    private BigDecimal cashLimit = new BigDecimal("20000.00");
    private int roundingScale = 2;
}
