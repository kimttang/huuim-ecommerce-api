package com.huuim.ecommerce.user.repository;

import com.huuim.ecommerce.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 로그인 시 아이디로 회원을 찾기 위한 커스텀 메서드
    Optional<User> findByLoginId(String loginId);
}