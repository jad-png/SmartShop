package com.jyoxin.smartshop.service.impl;

import com.jyoxin.smartshop.config.PricingConfiguration;
import com.jyoxin.smartshop.core.exception.BusinessRuleException;
import com.jyoxin.smartshop.core.exception.ResourceNotFoundException;
import com.jyoxin.smartshop.dto.request.OrderItemRequest;
import com.jyoxin.smartshop.dto.response.OrderPricingDTO;
import com.jyoxin.smartshop.entity.Product;
import com.jyoxin.smartshop.entity.PromoCode;
import com.jyoxin.smartshop.entity.enums.Tier;
import com.jyoxin.smartshop.service.LoyaltyService;
import com.jyoxin.smartshop.service.PricingService;
import com.jyoxin.smartshop.service.PromoCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingServiceImpl implements PricingService {

    private final PromoCodeService promoCodeService;
    private final LoyaltyService loyaltyService;
    private final PricingConfiguration pricingConfiguration;

    @Override
    @Transactional(readOnly = true)
    public OrderPricingDTO calculatePricing(List<OrderItemRequest> items, Map<Long, Product> productMap,
            Tier clientTier, String promoCode) {
        BigDecimal subTotal = calculateSubTotal(items, productMap);
        int scale = pricingConfiguration.getRoundingScale();

        List<String> discountSources = new ArrayList<>();
        BigDecimal loyaltyDiscount = loyaltyService.calculateDiscount(clientTier, subTotal)
                .setScale(scale, RoundingMode.HALF_UP);
        BigDecimal promoDiscount = BigDecimal.ZERO.setScale(scale, RoundingMode.HALF_UP);
        String appliedPromoCode = null;

        if (loyaltyDiscount.compareTo(BigDecimal.ZERO) > 0) {
            discountSources.add("Loyalty " + clientTier);
        }

        if (promoCode != null && !promoCode.isBlank()) {
            try {
                PromoCode promo = promoCodeService.validateAndGet(promoCode);
                BigDecimal promoPercent = promo.getPercentage().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                promoDiscount = subTotal.multiply(promoPercent)
                        .setScale(scale, RoundingMode.HALF_UP);

                discountSources.add("Promo Code " + promo.getCode());
                appliedPromoCode = promo.getCode();
            } catch (ResourceNotFoundException e) {
                log.warn("Promo code not found: {}", promoCode);
            } catch (BusinessRuleException e) {
                log.warn("Promo code invalid: {} - {}", promoCode, e.getMessage());
            }
        }

        BigDecimal discountAmount = loyaltyDiscount.add(promoDiscount)
                .setScale(scale, RoundingMode.HALF_UP);

        BigDecimal amountAfterDiscount = subTotal.subtract(discountAmount);
        if (amountAfterDiscount.compareTo(BigDecimal.ZERO) < 0) {
            amountAfterDiscount = BigDecimal.ZERO;
        }
        amountAfterDiscount = amountAfterDiscount.setScale(scale, RoundingMode.HALF_UP);

        BigDecimal tvaAmount = amountAfterDiscount.multiply(pricingConfiguration.getTvaRate())
                .setScale(scale, RoundingMode.HALF_UP);

        BigDecimal totalTtc = amountAfterDiscount.add(tvaAmount)
                .setScale(scale, RoundingMode.HALF_UP);

        return OrderPricingDTO.builder()
                .subTotal(subTotal)
                .discountAmount(discountAmount)
                .loyaltyDiscount(loyaltyDiscount)
                .promoDiscount(promoDiscount)
                .tvaAmount(tvaAmount)
                .totalTtc(totalTtc)
                .discountSources(discountSources)
                .appliedPromoCode(appliedPromoCode)
                .build();
    }

    private BigDecimal calculateSubTotal(List<OrderItemRequest> items, Map<Long, Product> productMap) {
        BigDecimal subTotal = BigDecimal.ZERO;
        for (OrderItemRequest item : items) {
            Product product = productMap.get(item.getProductId());
            if (product == null) {
                throw new ResourceNotFoundException("Product not found with id: " + item.getProductId());
            }

            BigDecimal lineTotal = product.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subTotal = subTotal.add(lineTotal);
        }
        return subTotal.setScale(pricingConfiguration.getRoundingScale(), RoundingMode.HALF_UP);
    }
}
