package com.huuim.ecommerce.order.service;

import com.huuim.ecommerce.common.exception.OutOfStockException;
import com.huuim.ecommerce.order.domain.Order;
import com.huuim.ecommerce.order.repository.OrderRepository;
import com.huuim.ecommerce.product.domain.Product;
import com.huuim.ecommerce.product.repository.ProductRepository;
import com.huuim.ecommerce.user.domain.User;
import com.huuim.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    /**
     * 왜:
     * - 트랜잭션 시작 후 비관적 락으로 상품 row를 선점
     * - 해당 트랜잭션이 끝날 때까지 다른 트랜잭션은 접근 불가
     * - 재고 감소 로직의 원자성 보장
     */
    @Transactional
    public Long createOrder(Long userId, Long productId, int quantity) {

        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        // 2. 비관적 락으로 상품 조회
        Product product = productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        // 3. 재고 검증 및 차감
        if (product.getStock() < quantity) {
            throw new OutOfStockException();
        }

        product.removeStock(quantity);

        // 4. 주문 생성
        Order order = new Order(user, product, quantity);

        return orderRepository.save(order).getId();
    }
}