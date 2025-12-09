package com.jyoxin.smartshop.service;

import com.jyoxin.smartshop.dto.request.CreatePromoCodeRequest;
import com.jyoxin.smartshop.dto.response.PromoCodeDTO;
import com.jyoxin.smartshop.entity.PromoCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PromoCodeService {
    PromoCodeDTO createPromoCode(CreatePromoCodeRequest request);

    PromoCodeDTO getPromoCodeByCode(String code);

    Page<PromoCodeDTO> getAllPromoCodes(Pageable pageable);

    PromoCode validateAndGet(String code);

    void incrementUsage(String code);

    PromoCodeDTO deactivatePromoCode(String code);
}
