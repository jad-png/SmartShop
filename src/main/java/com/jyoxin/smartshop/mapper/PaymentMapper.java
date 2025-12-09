package com.jyoxin.smartshop.mapper;

import com.jyoxin.smartshop.dto.request.CreatePaymentRequest;
import com.jyoxin.smartshop.dto.response.PaymentDTO;
import com.jyoxin.smartshop.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    
    Payment toEntity(CreatePaymentRequest request);

    PaymentDTO toResponse(Payment payment);
}
