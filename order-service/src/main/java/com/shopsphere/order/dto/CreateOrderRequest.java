package com.shopsphere.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateOrderRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotEmpty(message = "Order must contain at least one item")
        List<@Valid OrderItemRequest> items
) {
}