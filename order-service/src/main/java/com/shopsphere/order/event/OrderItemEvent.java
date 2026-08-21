package com.shopsphere.order.event;

public record OrderItemEvent(
        Long productId,
        Integer quantity
) {
}