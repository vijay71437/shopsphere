package com.shopsphere.product.service;

import com.shopsphere.product.dto.CreateProductRequest;
import com.shopsphere.product.dto.ProductResponse;
import com.shopsphere.product.entity.Product;
import com.shopsphere.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(request.category())
                .active(true)
                .build();

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    @Override
    public ProductResponse getProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found with id: " + id)
                );

        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {

        return productRepository.findByCategory(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProductResponse updateProduct(
            Long id,
            CreateProductRequest request
    ) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found with id: " + id)
                );

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());

        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            throw new RuntimeException(
                    "Product not found with id: " + id
            );
        }

        productRepository.deleteById(id);
    }

    private ProductResponse mapToResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getActive(),
                product.getCreatedAt()
        );
    }
}