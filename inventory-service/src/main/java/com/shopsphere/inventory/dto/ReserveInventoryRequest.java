package com.shopsphere.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReserveInventoryRequest(

        @NotNull
        @Positive
        Integer quantity
) {
}