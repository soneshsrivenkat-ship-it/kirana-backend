package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.RefundRequest;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Refund Controller
 *
 * Handles refund-related operations.
 *
 * Base URL: /refund
 *
 * Responsibilities:
 * - Accept refund requests
 * - Delegate refund logic to RefundService
 * - Return standardized API responses
 *
 * Refund Flow:
 * 1. Client sends original transaction ID + product IDs
 * 2. Service validates transaction
 * 3. Creates a DEBIT transaction linked to original
 * 4. Restores product stock
 * 5. Returns refund transaction details
 */
@RestController
@RequestMapping("/refund")
@RequiredArgsConstructor
public class RefundController {

    // Business logic handler for refunds
    private final RefundService service;

    /**
     * Create a refund transaction.
     *
     * Endpoint: POST /refund
     *
     * Request Body:
     * {
     *   "transactionId": "original-transaction-id",
     *   "productIds": ["P001", "P002"]
     * }
     *
     * @param request Contains original transaction ID
     *                and list of product IDs to refund.
     *
     * @return ApiResponse containing the created refund transaction details.
     */
    @PostMapping
    public ApiResponse<TransactionResponse> refund(
            @RequestBody RefundRequest request) {

        return new ApiResponse<>(
                true,
                service.refund(request)
        );
    }
}
