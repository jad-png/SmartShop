package com.jyoxin.smartshop.core.exception;

import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {

    private final String ruleCode;

    public BusinessRuleException(String message) {
        super(message);
        this.ruleCode = null;
    }

    public BusinessRuleException(String message, String ruleCode) {
        super(message);
        this.ruleCode = ruleCode;
    }
}