package com.shopsphere.order.event;

import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        Long userId,
        List<OrderItemEvent> items
) {
}