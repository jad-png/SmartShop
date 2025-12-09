package com.jyoxin.smartshop.service.impl;

import com.jyoxin.smartshop.core.exception.BusinessRuleException;
import com.jyoxin.smartshop.core.exception.ResourceNotFoundException;
import com.jyoxin.smartshop.dto.request.CreateProductRequest;
import com.jyoxin.smartshop.dto.request.UpdateProductRequest;
import com.jyoxin.smartshop.dto.response.ProductDTO;
import com.jyoxin.smartshop.entity.Product;
import com.jyoxin.smartshop.mapper.ProductMapper;
import com.jyoxin.smartshop.repository.ProductRepository;
import com.jyoxin.smartshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        product.setDeleted(false);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findByIdAndDeletedFalse(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Override
    public ProductDTO getProductByIdIncludingDeleted(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable, String nameFilter) {
        if (nameFilter != null && !nameFilter.isBlank()) {
            return productRepository.findByNameContainingIgnoreCaseAndDeletedFalse(nameFilter, pageable)
                    .map(productMapper::toResponse);
        }
        return productRepository.findByDeletedFalse(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productMapper.updateEntityFromRequest(request, product);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (product.isDeleted()) {
            throw new BusinessRuleException("Product is already deleted", "PRODUCT_ALREADY_DELETED");
        }

        productRepository.delete(product);
    }
}
