package com.shopsphere.order.client;

import com.shopsphere.order.dto.InventoryResponse;
import com.shopsphere.order.dto.ReserveInventoryRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PostMapping("/api/inventory/{productId}/reserve")
    InventoryResponse reserveStock(
            @PathVariable("productId") Long productId,
            @RequestBody ReserveInventoryRequest request
    );

    @PostMapping("/api/inventory/{productId}/release")
    InventoryResponse releaseStock(
            @PathVariable("productId") Long productId,
            @RequestBody ReserveInventoryRequest request
    );
}