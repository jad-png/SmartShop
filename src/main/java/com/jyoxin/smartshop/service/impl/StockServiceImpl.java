package com.jyoxin.smartshop.service.impl;

import com.jyoxin.smartshop.core.exception.InsufficientStockException;
import com.jyoxin.smartshop.core.exception.ResourceNotFoundException;
import com.jyoxin.smartshop.dto.request.OrderItemRequest;
import com.jyoxin.smartshop.entity.Product;
import com.jyoxin.smartshop.repository.ProductRepository;
import com.jyoxin.smartshop.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isStockAvailable(Long productId, int quantity) {
        return productRepository.findById(productId)
                .map(product -> product.getStock() >= quantity)
                .orElse(false);
    }

    @Override
    @Transactional
    public void decrementStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (product.getStock() < quantity) {
            throw new InsufficientStockException(product.getName(), quantity, product.getStock());
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void incrementStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateStockForOrder(List<OrderItemRequest> items) {
        Map<String, InsufficientStockException.StockInfo> insufficientProducts = new HashMap<>();

        for (OrderItemRequest item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.getProductId()));

            if (product.getStock() < item.getQuantity()) {
                insufficientProducts.put(product.getName(),
                        new InsufficientStockException.StockInfo(item.getQuantity(), product.getStock()));
            }
        }

        if (!insufficientProducts.isEmpty()) {
            throw new InsufficientStockException(insufficientProducts);
        }
    }
}
