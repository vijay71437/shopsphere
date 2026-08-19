package com.shopsphere.inventory.service;

import com.shopsphere.inventory.dto.CreateInventoryRequest;
import com.shopsphere.inventory.dto.InventoryResponse;
import com.shopsphere.inventory.entity.Inventory;
import com.shopsphere.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl
        implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public InventoryResponse createInventory(
            CreateInventoryRequest request
    ) {

        if (inventoryRepository
                .findByProductId(request.productId())
                .isPresent()) {

            throw new RuntimeException(
                    "Inventory already exists for product: "
                            + request.productId()
            );
        }

        Inventory inventory = Inventory.builder()
                .productId(request.productId())
                .quantity(request.quantity())
                .reservedQuantity(0)
                .build();

        Inventory saved = inventoryRepository.save(inventory);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long productId) {

        Inventory inventory =
                inventoryRepository.findByProductId(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory not found for product: "
                                                + productId
                                )
                        );

        return mapToResponse(inventory);
    }

    @Override
    @Transactional
    public InventoryResponse addStock(
            Long productId,
            Integer quantity
    ) {

        Inventory inventory =
                inventoryRepository.findByProductId(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory not found for product: "
                                                + productId
                                )
                        );

        inventory.setQuantity(
                inventory.getQuantity() + quantity
        );

        Inventory saved = inventoryRepository.save(inventory);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public InventoryResponse reserveStock(
            Long productId,
            Integer quantity
    ) {

        Inventory inventory =
                inventoryRepository.findByProductId(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory not found for product: "
                                                + productId
                                )
                        );

        int available =
                inventory.getQuantity()
                        - inventory.getReservedQuantity();

        if (available < quantity) {
            throw new RuntimeException(
                    "Insufficient inventory for product: "
                            + productId
            );
        }

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() + quantity
        );

        Inventory saved =
                inventoryRepository.save(inventory);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public InventoryResponse releaseStock(
            Long productId,
            Integer quantity
    ) {

        Inventory inventory =
                inventoryRepository.findByProductId(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory not found for product: "
                                                + productId
                                )
                        );

        if (inventory.getReservedQuantity() < quantity) {
            throw new RuntimeException(
                    "Cannot release more stock than reserved"
            );
        }

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - quantity
        );

        Inventory saved =
                inventoryRepository.save(inventory);

        return mapToResponse(saved);
    }

    private InventoryResponse mapToResponse(
            Inventory inventory
    ) {

        int available =
                inventory.getQuantity()
                        - inventory.getReservedQuantity();

        return new InventoryResponse(
                inventory.getProductId(),
                inventory.getQuantity(),
                inventory.getReservedQuantity(),
                available
        );
    }
}