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
            @Valid @RequestBody OrderRequest request
    ) {
        User user = userService.authenticate(loginId, loginPw);

        return orderService.createOrder(
                user.getId(),
                request.productId(),
                request.quantity()
        );
    }
}