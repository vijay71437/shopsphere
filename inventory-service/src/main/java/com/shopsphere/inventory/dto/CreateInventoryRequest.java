package com.shopsphere.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateInventoryRequest(

        @NotNull
        Long productId,

        @NotNull
        @PositiveOrZero
        Integer quantity
) {
}