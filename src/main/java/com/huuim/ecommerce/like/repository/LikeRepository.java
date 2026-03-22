package com.huuim.ecommerce.like.repository;

import com.huuim.ecommerce.like.domain.Like;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndProductId(Long userId, Long productId);

    // 유저가 좋아요한 목록을 최신순으로 가져오기 (N+1 방지)
    @EntityGraph(attributePaths = {"product"})
    List<Like> findAllByUserIdOrderByIdDesc(Long userId);
}