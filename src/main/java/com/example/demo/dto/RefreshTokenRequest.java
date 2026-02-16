package com.example.demo.dto;

import lombok.Data;

/**
 * RefreshTokenRequest
 *
 * DTO used to request a new access token
 * using a valid refresh token.
 *
 * Purpose:
 * - Sent by client when access token expires
 * - Validates refresh token
 * - Generates new access token
 *
 * Used In:
 * POST /auth/refresh
 */
@Data
public class RefreshTokenRequest {

    /**
     * Refresh token issued during login.
     */
    private String refreshToken;
}
