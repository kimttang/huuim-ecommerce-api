package com.huuim.ecommerce.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId,

        @NotNull(message = "수량은 필수입니다.")
        @Min(value = 1, message = "주문 수량은 최소 1개 이상이어야 합니다.") // 음수, 0 차단
        Integer quantity
) {
}