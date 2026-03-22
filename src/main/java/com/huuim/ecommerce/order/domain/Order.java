package com.huuim.ecommerce.order.domain;

import com.huuim.ecommerce.common.entity.BaseEntity;
import com.huuim.ecommerce.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 왜:
     * - 주문은 항상 유저에 종속
     * - Lazy 로딩으로 불필요한 조회 방지
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 왜:
     * - 주문 1건에 여러 상품 포함
     * - cascade로 Order 저장 시 OrderItem 자동 저장
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(User user) {
        this.user = user;
    }

    /**
     * 왜:
     * - 연관관계 편의 메서드로 양방향 일관성 유지
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.assignOrder(this);
    }
}