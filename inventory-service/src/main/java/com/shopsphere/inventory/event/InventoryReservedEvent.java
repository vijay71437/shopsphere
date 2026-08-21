package com.shopsphere.inventory.event;

public record InventoryReservedEvent(
        Long orderId,
        Long productId,
        Integer quantity
) {
}