package com.shopsphere.order.service;

import com.shopsphere.order.dto.CreateOrderRequest;
import com.shopsphere.order.dto.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrder(Long id);
}