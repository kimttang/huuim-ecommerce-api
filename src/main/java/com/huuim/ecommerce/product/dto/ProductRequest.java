package com.huuim.ecommerce.product.dto;

public record ProductRequest(
        String name,
        Integer price,
        Integer stock
) {
}