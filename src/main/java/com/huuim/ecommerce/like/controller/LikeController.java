package com.huuim.ecommerce.like.controller;

import com.huuim.ecommerce.like.service.LikeService;
import com.huuim.ecommerce.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // 좋아요 등록
    @PostMapping("/products/{productId}/likes")
    public void like(
            @RequestHeader("X-Huuim-LoginId") String loginId,
            @RequestHeader("X-Huuim-LoginPw") String loginPw,
            @PathVariable Long productId
    ) {
        likeService.likeProduct(loginId, loginPw, productId);
    }

    //  좋아요 취소
    @DeleteMapping("/products/{productId}/likes")
    public void unlike(
            @RequestHeader("X-Huuim-LoginId") String loginId,
            @RequestHeader("X-Huuim-LoginPw") String loginPw,
            @PathVariable Long productId
    ) {
        likeService.unlikeProduct(loginId, loginPw, productId);
    }

    //  좋아요 목록 조회
    @GetMapping("/likes")
    public List<ProductResponse> getLikedProducts(
            @RequestHeader("X-Huuim-LoginId") String loginId,
            @RequestHeader("X-Huuim-LoginPw") String loginPw
    ) {
        return likeService.getLikedProducts(loginId, loginPw);
    }
}