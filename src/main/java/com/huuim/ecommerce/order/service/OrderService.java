package com.huuim.ecommerce.order.service;

import com.huuim.ecommerce.common.exception.OutOfStockException;
import com.huuim.ecommerce.order.domain.Order;
import com.huuim.ecommerce.order.domain.OrderItem;
import com.huuim.ecommerce.order.dto.OrderRequest;
import com.huuim.ecommerce.order.repository.OrderRepository;
import com.huuim.ecommerce.product.domain.Product;
import com.huuim.ecommerce.product.repository.ProductRepository;
import com.huuim.ecommerce.user.domain.User;
import com.huuim.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Long createOrder(Long userId, OrderRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        /**
         * 왜:
         * - 데드락 방지 핵심 전략
         * - 모든 트랜잭션이 동일한 순서로 row lock 획득하도록 강제
         */
        List<Long> sortedProductIds = request.items().stream()
                .map(OrderRequest.OrderItemRequest::productId)
                .distinct()
                .sorted()
                .toList();

        // 비관적 락으로 상품 조회
        List<Product> products = productRepository.findByIdInWithPessimisticLock(sortedProductIds);

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        Order order = new Order(user);

        for (OrderRequest.OrderItemRequest item : request.items()) {
            Product product = productMap.get(item.productId());

            if (product == null) {
                throw new IllegalArgumentException("존재하지 않는 상품이 포함되어 있습니다: " + item.productId());
            }

            // 왜: Product 엔티티 내부의 removeStock 메서드에서 자체적으로 재고 부족 예외를 던지도록 위임
            product.removeStock(item.quantity());

            OrderItem orderItem = new OrderItem(product, item.quantity());
            order.addOrderItem(orderItem);
        }

        return orderRepository.save(order).getId();
    }
}