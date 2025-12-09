package com.jyoxin.smartshop.service.impl;

import com.jyoxin.smartshop.core.exception.BusinessRuleException;
import com.jyoxin.smartshop.core.exception.ResourceNotFoundException;
import com.jyoxin.smartshop.dto.request.CreatePromoCodeRequest;
import com.jyoxin.smartshop.dto.response.PromoCodeDTO;
import com.jyoxin.smartshop.entity.PromoCode;
import com.jyoxin.smartshop.mapper.PromoCodeMapper;
import com.jyoxin.smartshop.repository.PromoCodeRepository;
import com.jyoxin.smartshop.service.PromoCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromoCodeServiceImpl implements PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeMapper promoCodeMapper;

    @Override
    @Transactional
    public PromoCodeDTO createPromoCode(CreatePromoCodeRequest request) {
        if (promoCodeRepository.existsByCode(request.getCode())) {
            throw new BusinessRuleException("Promo code already exists", "DUPLICATE_PROMO_CODE");
        }
        PromoCode promoCode = promoCodeMapper.toEntity(request);
        promoCode.setActive(true);
        promoCode.setCurrentUsage(0);
        return promoCodeMapper.toResponse(promoCodeRepository.save(promoCode));
    }

    @Override
    public PromoCodeDTO getPromoCodeByCode(String code) {
        return promoCodeRepository.findByCode(code)
                .map(promoCodeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found: " + code));
    }

    @Override
    public Page<PromoCodeDTO> getAllPromoCodes(Pageable pageable) {
        return promoCodeRepository.findAll(pageable)
                .map(promoCodeMapper::toResponse);
    }

    @Override
    public PromoCode validateAndGet(String code) {
        PromoCode promoCode = promoCodeRepository.findByCodeAndActiveTrue(code)
                .orElseThrow(() -> new BusinessRuleException("Invalid or inactive promo code: " + code,
                        "INVALID_PROMO_CODE"));

        if (!promoCode.hasRemainingUsage()) {
            throw new BusinessRuleException("Promo code has reached maximum usage limit: " + code,
                    "PROMO_CODE_EXHAUSTED");
        }

        return promoCode;
    }

    @Override
    @Transactional
    public void incrementUsage(String code) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found: " + code));

        promoCode.setCurrentUsage(promoCode.getCurrentUsage() + 1);

        if (!promoCode.hasRemainingUsage()) {
            promoCode.setActive(false);
            log.info("Promo code {} auto-deactivated: max usage reached", code);
        }

        promoCodeRepository.save(promoCode);
    }

    @Override
    @Transactional
    public PromoCodeDTO deactivatePromoCode(String code) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found: " + code));

        promoCode.setActive(false);
        log.info("Promo code {} manually deactivated by admin", code);
        return promoCodeMapper.toResponse(promoCodeRepository.save(promoCode));
    }
}
