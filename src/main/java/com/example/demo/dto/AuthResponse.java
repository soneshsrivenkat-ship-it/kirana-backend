package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * AuthResponse
 *
 * DTO returned after successful authentication.
 *
 * Purpose:
 * - Provides JWT tokens to the client
 * - Enables secure communication with protected endpoints
 *
 * Contains:
 * 1. Access Token  -> Short-lived token used to access secured APIs
 * 2. Refresh Token -> Long-lived token used to generate a new access token
 *
 * Typical Flow:
 * - User logs in
 * - Server validates credentials
 * - Server generates access + refresh tokens
 * - Client stores tokens securely
 */
@Data
@AllArgsConstructor
public class AuthResponse {

    /**
     * JWT Access Token.
     * Used in Authorization header:
     * Authorization: Bearer <accessToken>
     */
    private String accessToken;

    /**
     * JWT Refresh Token.
     * Used to request a new access token when it expires.
     */
    private String refreshToken;
}
