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
import com.huuim.ecommerce.product.dto.ProductResponse;
import java.util.List;

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
    // 좋아요 취소 로직
    @Transactional
    public void unlikeProduct(String loginId, String loginPw, Long productId) {
        User user = userService.authenticate(loginId, loginPw);

        Like like = likeRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요를 누르지 않은 상품입니다."));

        Product product = productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        likeRepository.delete(like);
        product.decreaseLikes(); // Product 엔티티에 만들어둔 메서드 활용!
    }

    // 좋아요 목록 조회 로직
    @Transactional(readOnly = true)
    public List<ProductResponse> getLikedProducts(String loginId, String loginPw) {
        User user = userService.authenticate(loginId, loginPw);

        return likeRepository.findAllByUserIdOrderByIdDesc(user.getId())
                .stream()
                .map(like -> ProductResponse.from(like.getProduct()))
                .toList();
    }
}