package com.jyoxin.smartshop.dto.response;

import com.jyoxin.smartshop.entity.ClientStats;
import com.jyoxin.smartshop.entity.enums.Tier;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class ClientDTO {
    private Long id;
    private String name;
    private String email;
    private Tier loyaltyTier;
    private ClientStats stats;
}