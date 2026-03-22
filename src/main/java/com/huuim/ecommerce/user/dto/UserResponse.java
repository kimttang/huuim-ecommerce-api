package com.huuim.ecommerce.user.dto;

import com.huuim.ecommerce.user.domain.User;

public record UserResponse(
        Long id,
        String loginId,
        String name,
        String role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getRole().name()
        );
    }
}