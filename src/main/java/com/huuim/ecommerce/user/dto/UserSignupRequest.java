package com.huuim.ecommerce.user.dto;

import com.huuim.ecommerce.user.domain.UserRole;

public record UserSignupRequest(
        String loginId,
        String loginPw,
        String name,
        UserRole role
) {
}