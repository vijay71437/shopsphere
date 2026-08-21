package com.shopsphere.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderCreatedTopic() {
        return new NewTopic(
                "order.created",
                3,
                (short) 1
        );
    }

    @Bean
    public NewTopic inventoryReservedTopic() {
        return new NewTopic(
                "inventory.reserved",
                3,
                (short) 1
        );
    }

    @Bean
    public NewTopic inventoryReservationFailedTopic() {
        return new NewTopic(
                "inventory.reservation.failed",
                3,
                (short) 1
        );
    }
}