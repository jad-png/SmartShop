package com.jyoxin.smartshop.mapper;

import com.jyoxin.smartshop.dto.response.OrderItemDTO;
import com.jyoxin.smartshop.dto.response.OrderDTO;
import com.jyoxin.smartshop.entity.Order;
import com.jyoxin.smartshop.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PaymentMapper.class})
public interface OrderMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientName", source = "client.name")
    OrderDTO toResponse(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemDTO toOrderItemDTO(OrderItem orderItem);
}
