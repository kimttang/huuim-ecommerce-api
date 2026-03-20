package com.huuim.ecommerce.product.service;

import com.huuim.ecommerce.product.domain.Product;
import com.huuim.ecommerce.product.dto.ProductRequest;
import com.huuim.ecommerce.product.dto.ProductResponse;
import com.huuim.ecommerce.product.repository.ProductRepository;
import com.huuim.ecommerce.user.domain.User;
import com.huuim.ecommerce.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;

    @Transactional
    public Long createProduct(String loginId, String loginPw, ProductRequest request) {
        User user = userService.authenticate(loginId, loginPw);
        userService.validateAdmin(user);

        Product product = new Product(
                request.name(),
                request.price(),
                request.stock()
        );

        return productRepository.save(product).getId();
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(String sort, int page, int size) {

        Sort sortCondition = switch (sort == null ? "latest" : sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "likes_desc" -> Sort.by(Sort.Direction.DESC, "likesCount");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        Pageable pageable = PageRequest.of(page, size, sortCondition);

        return productRepository.findAll(pageable)
                .map(ProductResponse::from);
    }
}