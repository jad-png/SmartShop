package com.jyoxin.smartshop.controller;

import com.jyoxin.smartshop.core.annotation.Authenticated;
import com.jyoxin.smartshop.core.annotation.HasRole;
import com.jyoxin.smartshop.dto.request.CreatePromoCodeRequest;
import com.jyoxin.smartshop.dto.response.PromoCodeDTO;
import com.jyoxin.smartshop.entity.enums.Role;
import com.jyoxin.smartshop.service.PromoCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promo-codes")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @PostMapping
    @Authenticated
    @HasRole(Role.ADMIN)
    public ResponseEntity<PromoCodeDTO> createPromoCode(@Valid @RequestBody CreatePromoCodeRequest request) {
        return new ResponseEntity<>(promoCodeService.createPromoCode(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Authenticated
    @HasRole(Role.ADMIN)
    public ResponseEntity<Page<PromoCodeDTO>> getAllPromoCodes(Pageable pageable) {
        return ResponseEntity.ok(promoCodeService.getAllPromoCodes(pageable));
    }

    @GetMapping("/{code}")
    @Authenticated
    @HasRole(Role.ADMIN)
    public ResponseEntity<PromoCodeDTO> getPromoCodeByCode(@PathVariable String code) {
        return ResponseEntity.ok(promoCodeService.getPromoCodeByCode(code));
    }

    @PutMapping("/{code}/deactivate")
    @Authenticated
    @HasRole(Role.ADMIN)
    public ResponseEntity<PromoCodeDTO> deactivatePromoCode(@PathVariable String code) {
        return ResponseEntity.ok(promoCodeService.deactivatePromoCode(code));
    }
}
