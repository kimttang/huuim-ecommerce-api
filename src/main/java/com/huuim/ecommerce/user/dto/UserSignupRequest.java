package com.huuim.ecommerce.user.dto;

import com.huuim.ecommerce.user.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserSignupRequest(
        @NotBlank(message = "아이디는 필수입니다.")
        String loginId,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String loginPw,

        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotNull(message = "권한(역할)은 필수입니다.")
        UserRole role
) {
}