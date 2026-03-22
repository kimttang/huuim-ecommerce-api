package com.huuim.ecommerce.user.controller;

import com.huuim.ecommerce.user.dto.PasswordUpdateRequest;
import com.huuim.ecommerce.user.dto.UserResponse;
import com.huuim.ecommerce.user.dto.UserSignupRequest;
import com.huuim.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. 회원가입
    @PostMapping
    public Long signup(@Valid @RequestBody UserSignupRequest request) {
        return userService.signup(request);
    }

    // 2. 내 정보 조회
    @GetMapping("/me")
    public UserResponse getMyInfo(
            @RequestHeader("X-Huuim-LoginId") String loginId,
            @RequestHeader("X-Huuim-LoginPw") String loginPw
    ) {
        return userService.getMyInfo(loginId, loginPw);
    }

    // 3. 비밀번호 변경
    @PatchMapping("/me/password")
    public void updatePassword(
            @RequestHeader("X-Huuim-LoginId") String loginId,
            @RequestHeader("X-Huuim-LoginPw") String loginPw,
            @Valid @RequestBody PasswordUpdateRequest request
    ) {
        userService.updatePassword(loginId, loginPw, request.newPassword());
    }
}