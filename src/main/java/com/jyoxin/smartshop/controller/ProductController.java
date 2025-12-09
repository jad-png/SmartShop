package com.jyoxin.smartshop.controller;

import com.jyoxin.smartshop.core.annotation.Authenticated;
import com.jyoxin.smartshop.core.annotation.HasRole;
import com.jyoxin.smartshop.dto.request.CreateProductRequest;
import com.jyoxin.smartshop.dto.request.UpdateProductRequest;
import com.jyoxin.smartshop.dto.response.ProductDTO;
import com.jyoxin.smartshop.entity.enums.Role;
import com.jyoxin.smartshop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @HasRole(Role.ADMIN)
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return new ResponseEntity<>(productService.createProduct(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Authenticated
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            Pageable pageable,
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(productService.getAllProducts(pageable, name));
    }

    @GetMapping("/{id}")
    @Authenticated
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/{id}/admin")
    @HasRole(Role.ADMIN)
    public ResponseEntity<ProductDTO> getProductByIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductByIdIncludingDeleted(id));
    }

    @PutMapping("/{id}")
    @HasRole(Role.ADMIN)
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @HasRole(Role.ADMIN)
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
