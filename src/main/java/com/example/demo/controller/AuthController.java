package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.dto.*;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Register new user
     */
    @PostMapping("/register")
    public ApiResponse<String> register(
            @RequestBody RegisterRequest request) {

        userService.register(request);

        return new ApiResponse<>(
                true,
                "User registered successfully"
        );
    }

    /**
     * Login user -> Returns Access + Refresh token
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @RequestBody LoginRequest request) {

        AuthResponse response = userService.login(request);

        return new ApiResponse<>(
                true,
                response
        );
    }

    /**
     * Refresh Access Token using Refresh Token
     */
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(
            @RequestBody RefreshTokenRequest request) {

        AuthResponse response =
                userService.refreshToken(request);

        return new ApiResponse<>(
                true,
                response
        );
    }
}
