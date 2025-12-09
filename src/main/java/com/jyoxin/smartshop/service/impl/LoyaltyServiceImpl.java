package com.jyoxin.smartshop.service.impl;

import com.jyoxin.smartshop.config.LoyaltyProperties;
import com.jyoxin.smartshop.core.exception.ResourceNotFoundException;
import com.jyoxin.smartshop.entity.Client;
import com.jyoxin.smartshop.entity.ClientStats;
import com.jyoxin.smartshop.entity.enums.Tier;
import com.jyoxin.smartshop.repository.ClientRepository;
import com.jyoxin.smartshop.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoyaltyServiceImpl implements LoyaltyService {

    private final ClientRepository clientRepository;
    private final LoyaltyProperties loyaltyProperties;

    @Override
    public Tier calculateTier(ClientStats stats) {
        if (stats == null) return Tier.BASIC;

        if (isEligibleFor(stats, Tier.PLATINUM)) return Tier.PLATINUM;
        if (isEligibleFor(stats, Tier.GOLD)) return Tier.GOLD;
        if (isEligibleFor(stats, Tier.SILVER)) return Tier.SILVER;

        return Tier.BASIC;
    }

    @Override
    public BigDecimal calculateDiscount(Tier tier, BigDecimal subTotal) {
        if (tier == Tier.BASIC || subTotal == null) {
            return BigDecimal.ZERO;
        }

        LoyaltyProperties.TierSettings settings = loyaltyProperties.getTiers().get(tier);
        if (settings == null) return BigDecimal.ZERO;

        if (subTotal.compareTo(settings.getMinOrderAmountForDiscount()) < 0) {
            return BigDecimal.ZERO;
        }

        return subTotal.multiply(settings.getDiscountPercent())
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public void updateClientTier(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        Tier newTier = calculateTier(client.getStats());

        if (client.getLoyaltyTier() != newTier) {
            log.info("Client {} tier updated from {} to {}", client.getEmail(), client.getLoyaltyTier(), newTier);
            client.setLoyaltyTier(newTier);
            clientRepository.save(client);
        }
    }


    private boolean isEligibleFor(ClientStats stats, Tier tier) {
        LoyaltyProperties.TierSettings settings = loyaltyProperties.getTiers().get(tier);
        if (settings == null) return false;

        boolean hasEnoughOrders = stats.getTotalOrders() >= settings.getMinOrders();
        boolean hasSpentEnough = stats.getTotalSpent().compareTo(settings.getMinSpent()) >= 0;

        return hasEnoughOrders || hasSpentEnough;
    }
}