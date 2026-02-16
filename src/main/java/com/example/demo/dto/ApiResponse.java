package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApiResponse<T>
 *
 * Generic wrapper class for all API responses.
 *
 * Purpose:
 * - Standardizes API responses across the entire application
 * - Ensures consistent structure for success responses
 * - Makes frontend integration predictable
 *
 * Structure:
 * {
 *     "success": true/false,
 *     "data": { ... }
 * }
 *
 * @param <T> Type of the response payload
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    /**
     * Indicates whether the API request was successful.
     */
    private boolean success;

    /**
     * Actual response data payload.
     * Can be:
     * - DTO object
     * - List of DTOs
     * - String message
     * - Any custom response object
     */
    private T data;
}
