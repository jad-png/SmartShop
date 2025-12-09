package com.jyoxin.smartshop.mapper;

import com.jyoxin.smartshop.dto.request.CreateProductRequest;
import com.jyoxin.smartshop.dto.request.UpdateProductRequest;
import com.jyoxin.smartshop.dto.response.ProductDTO;
import com.jyoxin.smartshop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(CreateProductRequest request);

    ProductDTO toResponse(Product product);

    void updateEntityFromRequest(UpdateProductRequest request, @MappingTarget Product product);
}
