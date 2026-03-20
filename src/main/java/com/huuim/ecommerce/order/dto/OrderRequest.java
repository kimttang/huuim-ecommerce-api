package com.huuim.ecommerce.order.dto;

public record OrderRequest(
        Long productId,
        Integer quantity
) {
}