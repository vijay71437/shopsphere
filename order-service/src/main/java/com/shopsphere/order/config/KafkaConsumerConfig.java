package com.shopsphere.order.config;

import com.shopsphere.order.event.InventoryReservationFailedEvent;
import com.shopsphere.order.event.InventoryReservedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private Map<String, Object> baseProperties() {

        Map<String, Object> properties = new HashMap<>();

        properties.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092"
        );

        properties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        properties.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        return properties;
    }

    @Bean
    public ConsumerFactory<String, InventoryReservedEvent>
    inventoryReservedConsumerFactory() {

        Map<String, Object> properties = baseProperties();

        properties.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "order-service-reserved"
        );

        properties.put(
                "spring.json.value.default.type",
                InventoryReservedEvent.class.getName()
        );

        properties.put(
                "spring.json.use.type.headers",
                false
        );

        properties.put(
                "spring.json.trusted.packages",
                "com.shopsphere.order.event"
        );

        JacksonJsonDeserializer<InventoryReservedEvent> deserializer =
                new JacksonJsonDeserializer<>(
                        InventoryReservedEvent.class
                );

        return new DefaultKafkaConsumerFactory<>(
                properties,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReservedEvent>
    inventoryReservedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, InventoryReservedEvent>
                factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                inventoryReservedConsumerFactory()
        );

        return factory;
    }


    @Bean
    public ConsumerFactory<String, InventoryReservationFailedEvent>
    inventoryReservationFailedConsumerFactory() {

        Map<String, Object> properties = baseProperties();

        properties.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "order-service-failure"
        );

        properties.put(
                "spring.json.value.default.type",
                InventoryReservationFailedEvent.class.getName()
        );

        properties.put(
                "spring.json.use.type.headers",
                false
        );

        properties.put(
                "spring.json.trusted.packages",
                "com.shopsphere.order.event"
        );

        JacksonJsonDeserializer<InventoryReservationFailedEvent> deserializer =
                new JacksonJsonDeserializer<>(
                        InventoryReservationFailedEvent.class
                );

        return new DefaultKafkaConsumerFactory<>(
                properties,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReservationFailedEvent>
    inventoryReservationFailedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, InventoryReservationFailedEvent>
                factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                inventoryReservationFailedConsumerFactory()
        );

        return factory;
    }
}