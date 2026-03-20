package com.huuim.ecommerce.order.service;

import com.huuim.ecommerce.product.domain.Product;
import com.huuim.ecommerce.product.repository.ProductRepository;
import com.huuim.ecommerce.user.domain.User;
import com.huuim.ecommerce.user.domain.UserRole;
import com.huuim.ecommerce.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderServiceConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private Long productId;
    private Long userId;

    @BeforeEach
    void setup() {
        User user = userRepository.save(
                new User("test", "1234", "테스터", UserRole.USER)
        );

        Product product = productRepository.save(
                new Product("상품", 1000, 100)
        );

        userId = user.getId();
        productId = product.getId();
    }

    @Test
    void 동시에_100명이_주문하면_재고는_정확히_0이_된다() throws InterruptedException {

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    orderService.createOrder(userId, productId, 1);
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Product product = productRepository.findById(productId).orElseThrow();

        /**
         * 왜:
         * - 비관적 락이 없다면 stock < 0 또는 race condition 발생
         * - 비관적 락 적용 시 정확히 0 보장
         */
        assertThat(product.getStock()).isEqualTo(0);
    }
}