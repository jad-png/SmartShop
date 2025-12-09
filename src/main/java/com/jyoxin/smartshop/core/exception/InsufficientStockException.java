package com.jyoxin.smartshop.core.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class InsufficientStockException extends BusinessRuleException {

    private final Map<String, StockInfo> insufficientProducts;

    public InsufficientStockException(String productName, int requested, int available) {
        super(String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d",
                productName, requested, available), "INSUFFICIENT_STOCK");
        this.insufficientProducts = new HashMap<>();
        this.insufficientProducts.put(productName, new StockInfo(requested, available));
    }

    public InsufficientStockException(String message) {
        super(message, "INSUFFICIENT_STOCK");
        this.insufficientProducts = new HashMap<>();
    }

    public InsufficientStockException(Map<String, StockInfo> insufficientProducts) {
        super("Insufficient stock for one or more products", "INSUFFICIENT_STOCK");
        this.insufficientProducts = insufficientProducts;
    }

    public record StockInfo(int requested, int available) {}
}