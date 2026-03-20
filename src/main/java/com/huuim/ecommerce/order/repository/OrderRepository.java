package com.huuim.ecommerce.order.repository;

import com.huuim.ecommerce.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // 주문은 기본 CRUD만 사용하므로 추가 메서드 없음
}