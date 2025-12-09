package com.jyoxin.smartshop.config;

import com.jyoxin.smartshop.entity.enums.Tier;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "smartshop.loyalty")
@Data
public class LoyaltyProperties {

    private Map<Tier, TierSettings> tiers;

    @Data
    public static class TierSettings {
        private int minOrders;
        private BigDecimal minSpent;
        private BigDecimal discountPercent;
        private BigDecimal minOrderAmountForDiscount;
    }
}