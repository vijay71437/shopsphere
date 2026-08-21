package com.shopsphere.inventory.messaging;

import com.shopsphere.inventory.event.InventoryReservationFailedEvent;
import com.shopsphere.inventory.event.InventoryReservedEvent;
import com.shopsphere.inventory.event.OrderCreatedEvent;
import com.shopsphere.inventory.event.OrderItemEvent;
import com.shopsphere.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final InventoryService inventoryService;
    private final InventoryEventProducer inventoryEventProducer;

    @KafkaListener(
            topics = "order.created",
            groupId = "inventory-service"
    )
    public void handleOrderCreated(OrderCreatedEvent event) {

        System.out.println(
                "========== KAFKA EVENT RECEIVED =========="
        );

        try {

            for (OrderItemEvent item : event.items()) {

                System.out.println(
                        "Reserving product="
                                + item.productId()
                                + ", quantity="
                                + item.quantity()
                );

                inventoryService.reserveStock(
                        item.productId(),
                        item.quantity()
                );

                inventoryEventProducer.publishInventoryReserved(
                        new InventoryReservedEvent(
                                event.orderId(),
                                item.productId(),
                                item.quantity()
                        )
                );

                System.out.println(
                        "Inventory reserved for product: "
                                + item.productId()
                );
            }

        } catch (RuntimeException exception) {

            System.out.println(
                    "Inventory reservation failed for order: "
                            + event.orderId()
            );

            inventoryEventProducer.publishInventoryReservationFailed(
                    new InventoryReservationFailedEvent(
                            event.orderId(),
                            null,
                            null,
                            exception.getMessage()
                    )
            );
        }
    }
}