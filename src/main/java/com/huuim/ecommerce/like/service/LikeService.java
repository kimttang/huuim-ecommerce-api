package com.huuim.ecommerce.like.service;

import com.huuim.ecommerce.like.domain.Like;
import com.huuim.ecommerce.like.repository.LikeRepository;
import com.huuim.ecommerce.product.domain.Product;
import com.huuim.ecommerce.product.repository.ProductRepository;
import com.huuim.ecommerce.user.domain.User;
import com.huuim.ecommerce.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    @Transactional
    public void likeProduct(String loginId, String loginPw, Long productId) {

        User user = userService.authenticate(loginId, loginPw);

        if (likeRepository.findByUserIdAndProductId(user.getId(), productId).isPresent()) {
            throw new IllegalArgumentException("이미 좋아요를 누른 상품");
        }

        Product product = productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        likeRepository.save(new Like(user, product));

        product.increaseLikes();
    }
}