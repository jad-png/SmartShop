package com.jyoxin.smartshop.service;

import com.jyoxin.smartshop.dto.request.OrderItemRequest;
import com.jyoxin.smartshop.dto.response.OrderPricingDTO;
import com.jyoxin.smartshop.entity.Product;
import com.jyoxin.smartshop.entity.enums.Tier;

import java.util.List;
import java.util.Map;

public interface PricingService {
    OrderPricingDTO calculatePricing(List<OrderItemRequest> items, Map<Long, Product> productMap, Tier clientTier,
            String promoCode);
}
