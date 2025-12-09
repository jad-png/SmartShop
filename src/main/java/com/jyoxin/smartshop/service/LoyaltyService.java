package com.jyoxin.smartshop.service;

import com.jyoxin.smartshop.entity.ClientStats;
import com.jyoxin.smartshop.entity.enums.Tier;
import java.math.BigDecimal;

public interface LoyaltyService {


    Tier calculateTier(ClientStats stats);


    BigDecimal calculateDiscount(Tier tier, BigDecimal subTotal);


    void updateClientTier(Long clientId);
}