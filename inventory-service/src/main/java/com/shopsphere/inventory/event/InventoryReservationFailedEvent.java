package com.shopsphere.inventory.event;

public record InventoryReservationFailedEvent(
        Long orderId,
        Long productId,
        Integer quantity,
        String reason
) {
}