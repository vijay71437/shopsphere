package com.shopsphere.order.event;

public record InventoryReservedEvent(
        Long orderId,
        Long productId,
        Integer quantity
) {
}
