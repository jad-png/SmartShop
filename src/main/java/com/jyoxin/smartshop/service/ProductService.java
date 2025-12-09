package com.jyoxin.smartshop.service;

import com.jyoxin.smartshop.dto.request.CreateProductRequest;
import com.jyoxin.smartshop.dto.request.UpdateProductRequest;
import com.jyoxin.smartshop.dto.response.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductDTO createProduct(CreateProductRequest request);

    ProductDTO getProductById(Long id);

    ProductDTO getProductByIdIncludingDeleted(Long id);

    Page<ProductDTO> getAllProducts(Pageable pageable, String nameFilter);

    ProductDTO updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);
}
