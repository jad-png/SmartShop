package com.jyoxin.smartshop.mapper;

import com.jyoxin.smartshop.dto.request.CreatePromoCodeRequest;
import com.jyoxin.smartshop.dto.response.PromoCodeDTO;
import com.jyoxin.smartshop.entity.PromoCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PromoCodeMapper {

    PromoCode toEntity(CreatePromoCodeRequest request);

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "remainingUsage", expression = "java(promoCode.getRemainingUsage())")
    PromoCodeDTO toResponse(PromoCode promoCode);
}
