package com.jyoxin.smartshop.service.impl;

import com.jyoxin.smartshop.config.PricingConfiguration;
import com.jyoxin.smartshop.core.exception.BusinessRuleException;
import com.jyoxin.smartshop.core.exception.ResourceNotFoundException;
import com.jyoxin.smartshop.dto.request.CreateOrderRequest;
import com.jyoxin.smartshop.dto.request.OrderItemRequest;
import com.jyoxin.smartshop.dto.response.OrderDTO;
import com.jyoxin.smartshop.dto.response.OrderPricingDTO;
import com.jyoxin.smartshop.entity.Client;
import com.jyoxin.smartshop.entity.Order;
import com.jyoxin.smartshop.entity.OrderItem;
import com.jyoxin.smartshop.entity.Product;
import com.jyoxin.smartshop.entity.enums.OrderStatus;
import com.jyoxin.smartshop.mapper.OrderMapper;
import com.jyoxin.smartshop.repository.ClientRepository;
import com.jyoxin.smartshop.repository.OrderRepository;
import com.jyoxin.smartshop.repository.ProductRepository;
import com.jyoxin.smartshop.service.LoyaltyService;
import com.jyoxin.smartshop.service.OrderService;
import com.jyoxin.smartshop.service.PricingService;
import com.jyoxin.smartshop.service.PromoCodeService;
import com.jyoxin.smartshop.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final StockService stockService;
    private final LoyaltyService loyaltyService;
    private final PricingService pricingService;
    private final PromoCodeService promoCodeService;
    private final PricingConfiguration pricingConfiguration;

    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + request.getClientId()));

        validateNoDuplicateProducts(request.getItems());

        Map<Long, Product> productMap = fetchProducts(request.getItems());

        boolean stockSufficient = true;
        StringBuilder insufficientItems = new StringBuilder();
        for (OrderItemRequest item : request.getItems()) {
            Product product = productMap.get(item.getProductId());

            if (product.getStock() < item.getQuantity()) {
                stockSufficient = false;
                insufficientItems.append(String.format("%s (Requested: %d, Available: %d); ",
                        product.getName(), item.getQuantity(), product.getStock()));
            }
        }

        OrderPricingDTO pricing = pricingService.calculatePricing(
                request.getItems(),
                productMap,
                client.getLoyaltyTier(),
                request.getPromoCode());

        BigDecimal tvaRate = pricingConfiguration.getTvaRate();
        BigDecimal subTotal = pricing.getSubTotal();
        BigDecimal discountAmount = pricing.getDiscountAmount();
        BigDecimal tvaAmount = pricing.getTvaAmount();
        BigDecimal totalTtc = pricing.getTotalTtc();

        OrderStatus initialStatus = stockSufficient ? OrderStatus.PENDING : OrderStatus.REJECTED;

        Order order = Order.builder()
                .client(client)
                .subTotal(subTotal)
                .discountAmount(discountAmount)
                .tvaRate(tvaRate)
                .tvaAmount(tvaAmount)
                .totalTtc(totalTtc)
                .remainingAmount(totalTtc)
                .promoCode(pricing.getAppliedPromoCode())
                .status(initialStatus)
                .items(new ArrayList<>())
                .build();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productMap.get(itemRequest.getProductId());

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getUnitPrice())
                    .lineTotal(product.getUnitPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                    .build();

            order.addItem(orderItem);
        }

        if (!stockSufficient) {
            log.warn("Order rejected due to insufficient stock: {}", insufficientItems);
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Override
    public Page<OrderDTO> getOrdersByClientId(Long clientId, Pageable pageable) {
        return orderRepository.findByClientId(clientId, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional
    public OrderDTO confirmOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING orders can be confirmed", "INVALID_ORDER_STATUS");
        }

        if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessRuleException("Order cannot be confirmed until fully paid", "ORDER_NOT_PAID");
        }

        for (OrderItem item : order.getItems()) {
            stockService.decrementStock(item.getProduct().getId(), item.getQuantity());
        }

        if (order.getPromoCode() != null) {
            promoCodeService.incrementUsage(order.getPromoCode());
        }

        Client client = order.getClient();
        client.recordOrder(order.getTotalTtc());
        clientRepository.save(client);

        loyaltyService.updateClientTier(client.getId());

        order.setStatus(OrderStatus.CONFIRMED);
        log.info("Order {} confirmed for client: {}", order.getId(), client.getName());
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING orders can be canceled", "INVALID_ORDER_STATUS");
        }

        order.setStatus(OrderStatus.CANCELED);
        log.info("Order {} canceled", order.getId());
        return orderMapper.toResponse(orderRepository.save(order));
    }

    private void validateNoDuplicateProducts(List<OrderItemRequest> items) {
        Set<Long> uniqueProductIds = new HashSet<>();
        List<Long> duplicateProductIds = new ArrayList<>();

        for (OrderItemRequest item : items) {
            if (!uniqueProductIds.add(item.getProductId())) {
                duplicateProductIds.add(item.getProductId());
            }
        }

        if (!duplicateProductIds.isEmpty()) {
            throw new BusinessRuleException(
                    "Duplicate products found in order. Product IDs: " + duplicateProductIds +
                    ". Please combine quantities for the same product.",
                    "DUPLICATE_PRODUCTS");
        }
    }

    private Map<Long, Product> fetchProducts(List<OrderItemRequest> items) {
        Map<Long, Product> productMap = new HashMap<>();
        for (OrderItemRequest item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + item.getProductId()));

            if (product.isDeleted()) {
                throw new BusinessRuleException(
                        "Product '" + product.getName() + "' is no longer available",
                        "PRODUCT_UNAVAILABLE");
            }

            productMap.put(item.getProductId(), product);
        }
        return productMap;
    }
}
