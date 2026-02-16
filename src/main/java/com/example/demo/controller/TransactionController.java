package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.TransactionRequest;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Transaction Controller
 *
 * Handles all transaction-related operations including:
 * - Creating transactions
 * - Fetching by ID
 * - Fetching all transactions
 * - Filtering by currency
 * - Filtering by date range
 *
 * Base URL: /transactions
 *
 * Business logic is delegated to TransactionService.
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    // Service layer responsible for transaction business logic
    private final TransactionService service;

    /**
     * Create a new transaction.
     *
     * Endpoint: POST /transactions
     *
     * @param request TransactionRequest DTO containing:
     *                - Products
     *                - Currency
     *                - Transaction details
     *
     * @return ApiResponse containing created TransactionResponse
     */
    @PostMapping
    public ApiResponse<TransactionResponse> create(
            @RequestBody TransactionRequest request) {

        return new ApiResponse<>(
                true,
                service.createTransaction(request)
        );
    }

    /**
     * Fetch a transaction by ID.
     *
     * Endpoint: GET /transactions/{id}
     *
     * @param id Transaction unique identifier
     * @return ApiResponse containing TransactionResponse
     */
    @GetMapping("/{id}")
    public ApiResponse<TransactionResponse> getById(
            @PathVariable String id) {

        return new ApiResponse<>(
                true,
                service.getTransactionById(id)
        );
    }

    /**
     * Fetch all transactions.
     *
     * Endpoint: GET /transactions
     *
     * @return ApiResponse containing list of TransactionResponse
     */
    @GetMapping
    public ApiResponse<List<TransactionResponse>> getAll() {

        return new ApiResponse<>(
                true,
                service.getAllTransactions()
        );
    }

    /**
     * Fetch transactions by currency type.
     *
     * Endpoint: GET /transactions/currency/{currency}
     *
     * Example:
     * /transactions/currency/INR
     *
     * @param currency Currency code (e.g., INR, USD)
     * @return ApiResponse containing filtered transactions
     */
    @GetMapping("/currency/{currency}")
    public ApiResponse<List<TransactionResponse>> getByCurrency(
            @PathVariable String currency) {

        return new ApiResponse<>(
                true,
                service.getByCurrency(currency)
        );
    }

    /**
     * Fetch transactions within a date range.
     *
     * Endpoint: GET /transactions/sort
     *
     * Example:
     * /transactions/sort?from=2026-02-01T00:00:00&to=2026-02-16T23:59:59
     *
     * @param from Start date-time (inclusive)
     * @param to   End date-time (inclusive)
     *
     * @return ApiResponse containing transactions within the range
     */
    @GetMapping("/sort")
    public ApiResponse<List<TransactionResponse>> getByTime(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to) {

        return new ApiResponse<>(
                true,
                service.getByTime(from, to)
        );
    }
}
