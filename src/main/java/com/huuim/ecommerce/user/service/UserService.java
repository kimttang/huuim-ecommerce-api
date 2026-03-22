package com.huuim.ecommerce.user.service;

import com.huuim.ecommerce.user.domain.User;
import com.huuim.ecommerce.user.domain.UserRole;
import com.huuim.ecommerce.user.dto.UserSignupRequest;
import com.huuim.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long signup(UserSignupRequest request) {
        if (userRepository.findByLoginId(request.loginId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        User user = new User(
                request.loginId(),
                request.loginPw(),
                request.name(),
                request.role()
        );
        return userRepository.save(user).getId();
    }

    /**
     * 왜:
     * - 모든 인증 로직을 한 곳으로 집중시켜 중복 제거
     * - 단순 과제 조건이므로 password 평문 비교
     */
    @Transactional(readOnly = true)
    public User authenticate(String loginId, String loginPw) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        if (!user.getLoginPw().equals(loginPw)) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }

        return user;
    }

    public void validateAdmin(User user) {
        if (user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("권한 없음");
        }
    }
}