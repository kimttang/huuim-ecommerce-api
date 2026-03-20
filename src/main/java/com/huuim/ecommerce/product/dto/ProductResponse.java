package com.huuim.ecommerce.product.dto;

import com.huuim.ecommerce.product.domain.Product;

import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        Integer price,
        Integer stock,
        Integer likesCount,
        LocalDateTime createdAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getLikesCount(),
                product.getCreatedAt()
        );
    }
}