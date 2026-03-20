package com.huuim.ecommerce.product.controller;

import com.huuim.ecommerce.product.dto.ProductRequest;
import com.huuim.ecommerce.product.dto.ProductResponse;
import com.huuim.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public Long createProduct(
            @RequestHeader("X-Huuim-LoginId") String loginId,
            @RequestHeader("X-Huuim-LoginPw") String loginPw,
            @RequestBody ProductRequest request
    ) {
        return productService.createProduct(loginId, loginPw, request);
    }

    @GetMapping
    public Page<ProductResponse> getProducts(
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return productService.getProducts(sort, page, size);
    }
}