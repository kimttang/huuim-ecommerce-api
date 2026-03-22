package com.huuim.ecommerce.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderRequest(
        @NotEmpty(message = "주문할 상품이 최소 1개 이상 필요합니다.")
        @Valid // 내부 리스트 객체의 검증을 위해 필수
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            @NotNull(message = "상품 ID는 필수입니다.")
            Long productId,

            @NotNull(message = "수량은 필수입니다.")
            @Min(value = 1, message = "주문 수량은 최소 1개 이상이어야 합니다.")
            Integer quantity
    ) {}
}