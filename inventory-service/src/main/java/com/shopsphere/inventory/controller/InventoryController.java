package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.CreateInventoryRequest;
import com.shopsphere.inventory.dto.InventoryResponse;
import com.shopsphere.inventory.dto.ReserveInventoryRequest;
import com.shopsphere.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse createInventory(
            @Valid @RequestBody CreateInventoryRequest request
    ) {
        return inventoryService.createInventory(request);
    }

    @GetMapping("/{productId}")
    public InventoryResponse getInventory(
            @PathVariable Long productId
    ) {
        return inventoryService.getInventory(productId);
    }

    @PostMapping("/{productId}/stock")
    public InventoryResponse addStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity
    ) {
        return inventoryService.addStock(
                productId,
                quantity
        );
    }

    @PostMapping("/{productId}/reserve")
    public InventoryResponse reserveStock(
            @PathVariable Long productId,
            @Valid @RequestBody ReserveInventoryRequest request
    ) {
        return inventoryService.reserveStock(
                productId,
                request.quantity()
        );
    }

    @PostMapping("/{productId}/release")
    public InventoryResponse releaseStock(
            @PathVariable Long productId,
            @Valid @RequestBody ReserveInventoryRequest request
    ) {
        return inventoryService.releaseStock(
                productId,
                request.quantity()
        );
    }
}