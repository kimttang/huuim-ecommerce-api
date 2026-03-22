package com.huuim.ecommerce.product.domain;

import com.huuim.ecommerce.common.entity.BaseEntity;
import com.huuim.ecommerce.common.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_price", columnList = "price"),
                @Index(name = "idx_likes_count", columnList = "likesCount"),
                @Index(name = "idx_created_at", columnList = "createdAt")
        }
)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Integer likesCount = 0;

    @Version
    private Long version; // 동시성 제어 (Optimistic Lock)

    public Product(String name, Integer price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    /**
     * 왜: 재고는 동시성 이슈가 가장 큰 필드이므로 외부에서 직접 변경을 막고
     *     반드시 검증 로직을 통해서만 변경되도록 강제
     */
    public void removeStock(int quantity) {
        if (this.stock < quantity) {
            throw new OutOfStockException();
        }
        this.stock -= quantity;
    }

    public void addStock(int quantity) {
        this.stock += quantity;
    }

    public void increaseLikes() {
        this.likesCount++;
    }

    public void decreaseLikes() {
        if (this.likesCount > 0) {
            this.likesCount--;
        }
    }
}