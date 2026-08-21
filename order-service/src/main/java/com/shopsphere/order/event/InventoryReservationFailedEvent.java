package com.shopsphere.order.event;

public record InventoryReservationFailedEvent(
        Long orderId,
        Long productId,
        Integer quantity,
        String reason
) {
}
