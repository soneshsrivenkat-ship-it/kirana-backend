package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * UserResponse
 *
 * DTO representing user details returned to client.
 *
 * Purpose:
 * - Sent after authentication
 * - Used in admin APIs
 * - Exposes safe user information (no password)
 *
 * Security Note:
 * - Password is NEVER exposed
 * - Only minimal identity fields are returned
 */
@Data
@AllArgsConstructor
public class UserResponse {

    /**
     * Unique identifier of the user.
     */
    private String userId;

    /**
     * Username of the account.
     */
    private String username;

    /**
     * Role assigned to the user.
     * Example:
     * - ADMIN
     * - STAFF
     */
    private String role;
}
