package com.example.demo.dto;

import lombok.Data;

/**
 * LoginRequest
 *
 * DTO used for user authentication.
 *
 * Purpose:
 * - Captures user credentials from login API request
 * - Sent to AuthController during login
 *
 * Used In:
 * POST /auth/login
 *
 * Security Note:
 * Password should always be transmitted over HTTPS.
 */
@Data
public class LoginRequest {

    /**
     * Username of the user.
     * Must match a registered user in the system.
     */
    private String username;

    /**
     * Raw password entered by the user.
     * Will be validated against encrypted password stored in DB.
     */
    private String password;
}
