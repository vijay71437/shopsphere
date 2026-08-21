package com.shopsphere.inventory.messaging;

import com.shopsphere.inventory.event.InventoryReservedEvent;
import com.shopsphere.inventory.event.InventoryReservationFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishInventoryReserved(
            InventoryReservedEvent event
    ) {

        kafkaTemplate.send(
                "inventory.reserved",
                event.orderId().toString(),
                event
        );
    }

    public void publishInventoryReservationFailed(
            InventoryReservationFailedEvent event
    ) {

        kafkaTemplate.send(
                "inventory.reservation.failed",
                event.orderId().toString(),
                event
        );
    }
}