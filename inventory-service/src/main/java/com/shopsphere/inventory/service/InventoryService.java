package com.shopsphere.inventory.service;

import com.shopsphere.inventory.dto.CreateInventoryRequest;
import com.shopsphere.inventory.dto.InventoryResponse;

public interface InventoryService {

    InventoryResponse createInventory(
            CreateInventoryRequest request
    );

    InventoryResponse getInventory(Long productId);

    InventoryResponse addStock(
            Long productId,
            Integer quantity
    );
    InventoryResponse reserveStock(
            Long productId,
            Integer quantity
    );
    InventoryResponse releaseStock(
            Long productId,
            Integer quantity
    );
}