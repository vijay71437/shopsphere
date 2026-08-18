package com.shopsphere.order.service;

import com.shopsphere.order.client.ProductClient;
import com.shopsphere.order.dto.*;
import com.shopsphere.order.entity.Order;
import com.shopsphere.order.entity.OrderItem;
import com.shopsphere.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        Order order = Order.builder()
                .userId(request.userId())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {

            ProductResponse product =
                    productClient.getProduct(itemRequest.productId());

            if (!Boolean.TRUE.equals(product.active())) {
                throw new RuntimeException(
                        "Product is not active: " + product.id()
                );
            }

            BigDecimal itemTotal =
                    product.price()
                            .multiply(
                                    BigDecimal.valueOf(itemRequest.quantity())
                            );

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(product.id())
                    .quantity(itemRequest.quantity())
                    .price(product.price())
                    .build();

            order.getItems().add(orderItem);

            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found with id: " + id
                        )
                );

        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(item -> {

                            BigDecimal total =
                                    item.getPrice()
                                            .multiply(
                                                    BigDecimal.valueOf(
                                                            item.getQuantity()
                                                    )
                                            );

                            return new OrderItemResponse(
                                    item.getProductId(),
                                    null,
                                    item.getQuantity(),
                                    item.getPrice(),
                                    total
                            );
                        })
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                items,
                order.getCreatedAt()
        );
    }
}