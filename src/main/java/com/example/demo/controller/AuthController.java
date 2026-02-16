package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 *
 * This controller handles all authentication-related APIs.
 * It manages:
 * - User Registration
 * - User Login
 * - Access Token Refresh
 *
 * Base URL: /auth
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    // Service layer responsible for user authentication logic
    private final UserService userService;

    /**
     * Registers a new user.
     *
     * Endpoint: POST /auth/register
     *
     * Accepts user registration details such as:
     * - Username
     * - Email
     * - Password
     *
     * Calls the UserService to create a new user in the system.
     *
     * @param request Contains registration details
     * @return ApiResponse with success message
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
     * Authenticates a user and returns JWT tokens.
     *
     * Endpoint: POST /auth/login
     *
     * Validates:
     * - Username
     * - Password
     *
     * If authentication is successful:
     * - Generates Access Token
     * - Generates Refresh Token
     *
     * @param request Contains login credentials
     * @return ApiResponse containing AuthResponse (tokens)
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
     * Generates a new Access Token using a valid Refresh Token.
     *
     * Endpoint: POST /auth/refresh
     *
     * Used when:
     * - Access token expires
     * - Refresh token is still valid
     *
     * @param request Contains refresh token
     * @return ApiResponse containing new AuthResponse (new access token)
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
