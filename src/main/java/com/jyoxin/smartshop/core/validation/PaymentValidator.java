package com.jyoxin.smartshop.core.validation;

import com.jyoxin.smartshop.config.PricingConfiguration;
import com.jyoxin.smartshop.core.exception.BusinessRuleException;
import com.jyoxin.smartshop.dto.request.CreatePaymentRequest;
import com.jyoxin.smartshop.entity.enums.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PaymentValidator {

    private final PricingConfiguration pricingConfiguration;

    public void validate(CreatePaymentRequest request) {
        if (request.getMethod() == PaymentMethod.CASH) {
            validateCash(request);
        } else if (request.getMethod() == PaymentMethod.CHECK) {
            validateCheck(request);
        } else if (request.getMethod() == PaymentMethod.TRANSACTION) {
            validateTransaction(request);
        }
    }

    private void validateCash(CreatePaymentRequest request) {
        if (request.getAmount().compareTo(pricingConfiguration.getCashLimit()) > 0) {
            throw new BusinessRuleException("Cash payment exceeds limits of " + pricingConfiguration.getCashLimit(),
                    "CASH_LIMIT_EXCEEDED");
        }
    }

    private void validateCheck(CreatePaymentRequest request) {
        if (request.getReference() == null || request.getReference().isBlank()) {
            throw new BusinessRuleException("Check number (reference) is required", "MISSING_CHECK_NUMBER");
        }
        if (request.getBank() == null || request.getBank().isBlank()) {
            throw new BusinessRuleException("Bank is required for checks", "MISSING_BANK");
        }
        if (request.getDueDate() == null) {
            throw new BusinessRuleException("Due date is required for checks", "MISSING_DUE_DATE");
        }
    }

    private void validateTransaction(CreatePaymentRequest request) {
        if (request.getReference() == null || request.getReference().isBlank()) {
            throw new BusinessRuleException("Transaction reference is required", "MISSING_TRANSACTION_REF");
        }
        if (request.getBank() == null || request.getBank().isBlank()) {
            throw new BusinessRuleException("Bank (or Platform) is required for transactions", "MISSING_BANK");
        }
    }
}
