package com.shopsphere.order.messaging;

import com.shopsphere.order.entity.Order;
import com.shopsphere.order.entity.OrderStatus;
import com.shopsphere.order.event.InventoryReservationFailedEvent;
import com.shopsphere.order.event.InventoryReservedEvent;
import com.shopsphere.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(
            topics = "inventory.reservation.failed",
            containerFactory = "inventoryReservationFailedKafkaListenerContainerFactory"
    )
    public void handleInventoryReservationFailed(
            InventoryReservationFailedEvent event
    ) {

        System.out.println(
                "========== FAILURE EVENT RECEIVED =========="
        );

        System.out.println(
                "Order ID: " + event.orderId()
        );

        System.out.println(
                "Reason: " + event.reason()
        );

        Order order =
                orderRepository.findById(event.orderId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found: "
                                                + event.orderId()
                                )
                        );

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        System.out.println(
                "========== ORDER CANCELLED =========="
        );
    }

    @KafkaListener(
            topics = "inventory.reserved",
            containerFactory = "inventoryReservedKafkaListenerContainerFactory"
    )
    @Transactional
    public void handleInventoryReserved(
            InventoryReservedEvent event
    ) {

        System.out.println(
                "========== INVENTORY RESERVED =========="
        );

        System.out.println(
                "Order ID: " + event.orderId()
        );

        Order order =
                orderRepository.findById(
                        event.orderId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Order not found: "
                                        + event.orderId()
                        )
                );

        int reservedItems =
                order.getReservedItems() + 1;

        order.setReservedItems(reservedItems);

        if (reservedItems >= order.getTotalItems()) {

            order.setStatus(
                    OrderStatus.CONFIRMED
            );

            System.out.println(
                    "Order "
                            + order.getId()
                            + " CONFIRMED"
            );
        }

        orderRepository.save(order);
    }
}