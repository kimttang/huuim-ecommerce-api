package com.huuim.ecommerce.like.controller;

import com.huuim.ecommerce.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{productId}/likes")
    public void like(
            @RequestHeader("X-Huuim-LoginId") String loginId,
            @RequestHeader("X-Huuim-LoginPw") String loginPw,
            @PathVariable Long productId
    ) {
        likeService.likeProduct(loginId, loginPw, productId);
    }
}