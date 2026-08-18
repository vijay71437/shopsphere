package com.shopsphere.order.controller;

import com.shopsphere.order.dto.CreateOrderRequest;
import com.shopsphere.order.dto.OrderResponse;
import com.shopsphere.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(
            @PathVariable Long id
    ) {
        return orderService.getOrder(id);
    }
}