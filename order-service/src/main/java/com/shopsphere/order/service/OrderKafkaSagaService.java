package com.shopsphere.order.service;

import com.shopsphere.order.client.ProductClient;
import com.shopsphere.order.dto.CreateOrderRequest;
import com.shopsphere.order.dto.OrderItemRequest;
import com.shopsphere.order.dto.OrderItemResponse;
import com.shopsphere.order.dto.OrderResponse;
import com.shopsphere.order.dto.ProductResponse;
import com.shopsphere.order.entity.Order;
import com.shopsphere.order.entity.OrderItem;
import com.shopsphere.order.entity.OrderStatus;
import com.shopsphere.order.event.OrderCreatedEvent;
import com.shopsphere.order.event.OrderItemEvent;
import com.shopsphere.order.messaging.OrderEventProducer;
import com.shopsphere.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderKafkaSagaService {

    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @Transactional
    public OrderResponse createOrder(
            CreateOrderRequest request
    ) {

        // -------------------------------------------------
        // 1. Create Order
        // -------------------------------------------------

        Order order = Order.builder()
                .userId(request.userId())
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // -------------------------------------------------
        // 2. Validate products and create order items
        // -------------------------------------------------

        for (OrderItemRequest itemRequest : request.items()) {

            ProductResponse product =
                    productClient.getProduct(
                            itemRequest.productId()
                    );

            if (!Boolean.TRUE.equals(product.active())) {

                throw new RuntimeException(
                        "Product is not active: "
                                + product.id()
                );
            }

            BigDecimal itemTotal =
                    product.price()
                            .multiply(
                                    BigDecimal.valueOf(
                                            itemRequest.quantity()
                                    )
                            );

            OrderItem orderItem =
                    OrderItem.builder()
                            .order(order)
                            .productId(product.id())
                            .quantity(itemRequest.quantity())
                            .price(product.price())
                            .build();

            order.getItems().add(orderItem);

            totalAmount =
                    totalAmount.add(itemTotal);
        }

        // -------------------------------------------------
        // 3. Set total
        // -------------------------------------------------

        order.setTotalAmount(totalAmount);

        // -------------------------------------------------
        // 4. Save Order as PENDING
        // -------------------------------------------------

        Order savedOrder =
                orderRepository.save(order);

        // -------------------------------------------------
        // 5. Create Kafka Event
        // -------------------------------------------------

        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        savedOrder.getId(),
                        savedOrder.getUserId(),
                        savedOrder.getItems()
                                .stream()
                                .map(item ->
                                        new OrderItemEvent(
                                                item.getProductId(),
                                                item.getQuantity()
                                        )
                                )
                                .toList()
                );

        // -------------------------------------------------
        // 6. Publish event to Kafka
        // -------------------------------------------------

        orderEventProducer.publishOrderCreated(
                event
        );

        // -------------------------------------------------
        // 7. Return PENDING order
        // -------------------------------------------------

        return mapToResponse(savedOrder);
    }

    private OrderResponse mapToResponse(
            Order order
    ) {

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