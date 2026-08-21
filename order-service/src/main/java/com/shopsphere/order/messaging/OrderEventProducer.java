package com.shopsphere.order.messaging;

import com.shopsphere.order.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent>
            kafkaTemplate;

    public void publishOrderCreated(
            OrderCreatedEvent event
    ) {

        kafkaTemplate.send(
                "order.created",
                event.orderId().toString(),
                event
        );
    }
}