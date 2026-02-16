package com.example.demo.dto;

import lombok.Data;

/**
 * RegisterRequest
 *
 * DTO used for user registration.
 *
 * Purpose:
 * - Accepts user credentials
 * - Creates a new system user
 * - Assigns role-based access control
 *
 * Security:
 * - Password must be encrypted before saving
 * - Role defines authorization level (ADMIN / STAFF)
 *
 * Used In:
 * POST /auth/register
 */
@Data
public class RegisterRequest {

    /**
     * Unique username of the user.
     */
    private String username;

    /**
     * Raw password provided by the user.
     * Must be encoded before storing in DB.
     */
    private String password;

    /**
     * Role assigned to the user.
     * Example values:
     * - ADMIN
     * - STAFF
     */
    private String role; // ADMIN / STAFF
}
