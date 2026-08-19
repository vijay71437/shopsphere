package com.shopsphere.order.dto;

public record InventoryResponse(
        Long productId,
        Integer quantity,
        Integer reservedQuantity,
        Integer availableQuantity
) {
}