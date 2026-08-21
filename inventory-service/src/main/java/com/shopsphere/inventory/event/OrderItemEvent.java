package com.shopsphere.inventory.event;

public record OrderItemEvent(
        Long productId,
        Integer quantity
) {
}