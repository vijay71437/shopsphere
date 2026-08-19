package com.shopsphere.order.service;

import com.shopsphere.order.client.InventoryClient;
import com.shopsphere.order.client.ProductClient;
import com.shopsphere.order.dto.*;
import com.shopsphere.order.entity.Order;
import com.shopsphere.order.entity.OrderItem;
import com.shopsphere.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderSagaService {

    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(
            CreateOrderRequest request
    ) {

        // -------------------------------------------------
        // 1. Create Order object
        // -------------------------------------------------

        Order order = Order.builder()
                .userId(request.userId())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        /*
         * Keep track of every inventory reservation.
         *
         * If something fails later, we use this list
         * to compensate by releasing the reservations.
         */
        List<ReservedItem> reservedItems =
                new ArrayList<>();

        try {

            // -------------------------------------------------
            // 2. Process every order item
            // -------------------------------------------------

            for (OrderItemRequest itemRequest : request.items()) {

                // ---------------------------------------------
                // 2.1 Get product
                // ---------------------------------------------

                ProductResponse product =
                        productClient.getProduct(
                                itemRequest.productId()
                        );

                // ---------------------------------------------
                // 2.2 Validate product
                // ---------------------------------------------

                if (!Boolean.TRUE.equals(product.active())) {

                    throw new RuntimeException(
                            "Product is not active: "
                                    + product.id()
                    );
                }

                // ---------------------------------------------
                // 2.3 Reserve inventory
                // ---------------------------------------------

                InventoryResponse inventory =
                        inventoryClient.reserveStock(
                                itemRequest.productId(),
                                new ReserveInventoryRequest(
                                        itemRequest.quantity()
                                )
                        );

                // ---------------------------------------------
                // 2.4 Remember reservation
                // ---------------------------------------------

                reservedItems.add(
                        new ReservedItem(
                                itemRequest.productId(),
                                itemRequest.quantity()
                        )
                );

                // ---------------------------------------------
                // 2.5 Calculate item total
                // ---------------------------------------------

                BigDecimal itemTotal =
                        product.price()
                                .multiply(
                                        BigDecimal.valueOf(
                                                itemRequest.quantity()
                                        )
                                );

                // ---------------------------------------------
                // 2.6 Create OrderItem
                // ---------------------------------------------

                OrderItem orderItem =
                        OrderItem.builder()
                                .order(order)
                                .productId(product.id())
                                .quantity(
                                        itemRequest.quantity()
                                )
                                .price(product.price())
                                .build();

                order.getItems().add(orderItem);

                totalAmount =
                        totalAmount.add(itemTotal);
            }

            // -------------------------------------------------
            // 3. Set total amount
            // -------------------------------------------------

            order.setTotalAmount(totalAmount);

            // -------------------------------------------------
            // 4. Save order
            // -------------------------------------------------

            Order savedOrder =
                    orderRepository.save(order);

            // -------------------------------------------------
            // 5. Return successful order
            // -------------------------------------------------

            return mapToResponse(savedOrder);

        } catch (Exception exception) {

            // -------------------------------------------------
            // 6. Saga compensation
            // -------------------------------------------------

            compensateInventory(reservedItems);

            // -------------------------------------------------
            // 7. Propagate original failure
            // -------------------------------------------------

            throw exception;
        }
    }

    /**
     * Releases all inventory that was successfully
     * reserved before the failure occurred.
     */
    private void compensateInventory(
            List<ReservedItem> reservedItems
    ) {

        /*
         * Release in reverse order.
         *
         * Example:
         *
         * Reserve A
         * Reserve B
         * Reserve C
         *
         * If C fails:
         *
         * Release B
         * Release A
         */
        for (int i = reservedItems.size() - 1;
             i >= 0;
             i--) {

            ReservedItem reservedItem =
                    reservedItems.get(i);

            try {

                inventoryClient.releaseStock(
                        reservedItem.productId(),
                        new ReserveInventoryRequest(
                                reservedItem.quantity()
                        )
                );

            } catch (Exception compensationException) {

                /*
                 * Very important:
                 *
                 * Compensation can also fail.
                 *
                 * We should NOT hide the original
                 * exception.
                 *
                 * In the next version of the project,
                 * this will be handled through Kafka
                 * retry/DLQ mechanisms.
                 */

                System.err.println(
                        "CRITICAL: Failed to release inventory "
                                + "for productId="
                                + reservedItem.productId()
                );

                compensationException.printStackTrace();
            }
        }
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

    /**
     * Represents an inventory reservation that
     * successfully happened during this Saga.
     */
    private record ReservedItem(
            Long productId,
            Integer quantity
    ) {
    }
}