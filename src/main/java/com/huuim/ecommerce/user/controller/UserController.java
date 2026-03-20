package com.huuim.ecommerce.user.controller;

import com.huuim.ecommerce.user.dto.UserSignupRequest;
import com.huuim.ecommerce.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public Long signup(@RequestBody UserSignupRequest request) {
        return userService.signup(request);
    }
}