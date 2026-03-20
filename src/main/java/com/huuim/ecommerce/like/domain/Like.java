package com.huuim.ecommerce.like.domain;

import com.huuim.ecommerce.product.domain.Product;
import com.huuim.ecommerce.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(
        name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_product",
                        columnNames = {"user_id", "product_id"}
                )
        }
)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 왜: ManyToOne은 기본 FetchType.EAGER이므로 성능 문제 방지를 위해 LAZY로 명시
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    public Like(User user, Product product) {
        this.user = user;
        this.product = product;
    }
}