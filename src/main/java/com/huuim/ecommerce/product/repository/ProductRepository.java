package com.huuim.ecommerce.product.repository;

import com.huuim.ecommerce.product.domain.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 왜:
     * - 좋아요 기능 및 단건 처리에서 사용하는 비관적 락 (기존 코드 복구)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithPessimisticLock(@Param("id") Long id);

    /**
     * 왜:
     * - 다건 주문 시 데드락을 완벽하게 방지하기 위해 DB 레벨에서 id 오름차순으로 락 획득 (ORDER BY 추가)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id IN :ids ORDER BY p.id ASC")
    List<Product> findByIdInWithPessimisticLock(@Param("ids") List<Long> ids);
}