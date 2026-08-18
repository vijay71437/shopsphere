package com.shopsphere.product.service;

import com.shopsphere.product.dto.CreateProductRequest;
import com.shopsphere.product.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse getProduct(Long id);

    List<ProductResponse> getAllProducts();

    List<ProductResponse> getProductsByCategory(String category);

    ProductResponse updateProduct(Long id, CreateProductRequest request);

    void deleteProduct(Long id);
}