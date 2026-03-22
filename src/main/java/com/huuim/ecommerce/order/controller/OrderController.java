package com.huuim.ecommerce.order.controller;

import com.huuim.ecommerce.order.dto.OrderRequest;
import com.huuim.ecommerce.order.service.OrderService;
import com.huuim.ecommerce.user.domain.User;
import com.huuim.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping
    public Long createOrder(
            @RequestHeader("X-Huuim-LoginId") String loginId,
            @RequestHeader("X-Huuim-LoginPw") String loginPw,
            @Valid @RequestBody OrderRequest request // 리스트형 DTO
    ) {
        User user = userService.authenticate(loginId, loginPw);

        // Service 시그니처에 맞게 파라미터 전달 (유저ID, OrderRequest 통째로)
        return orderService.createOrder(user.getId(), request);
    }
}