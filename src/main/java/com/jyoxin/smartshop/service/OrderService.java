package com.jyoxin.smartshop.service;

import com.jyoxin.smartshop.dto.request.CreateOrderRequest;
import com.jyoxin.smartshop.dto.response.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDTO createOrder(CreateOrderRequest request);

    OrderDTO getOrderById(Long id);

    Page<OrderDTO> getOrdersByClientId(Long clientId, Pageable pageable);

    Page<OrderDTO> getAllOrders(Pageable pageable);

    OrderDTO confirmOrder(Long id);

    OrderDTO cancelOrder(Long id);
}
