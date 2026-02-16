package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.SalesSummaryResponse;
import com.example.demo.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Sales Controller
 *
 * Handles sales-related reporting operations.
 *
 * Base URL: /sales
 *
 * Responsibilities:
 * - Provide daily sales summaries
 * - Support multi-currency reporting
 * - Delegate business logic to SalesService
 *
 * This controller does not perform calculations directly.
 * All aggregation and business rules are handled in SalesService.
 */
@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesController {

    // Service responsible for sales calculations and aggregation
    private final SalesService salesService;

    /**
     * Retrieve daily sales summary.
     *
     * Endpoint: GET /sales/summary
     *
     * Example:
     * /sales/summary?currency=INR
     *
     * @param currency Currency code used to filter sales summary.
     *                 Defaults to "INR" if not provided.
     *
     * @return ApiResponse containing:
     *         - Total credits
     *         - Total debits
     *         - Net flow
     *         - Currency type
     */
    @GetMapping("/summary")
    public ApiResponse<SalesSummaryResponse> getSalesSummary(
            @RequestParam(defaultValue = "INR") String currency) {

        SalesSummaryResponse response =
                salesService.getSalesSummary(currency);

        return new ApiResponse<>(true, response);
    }
}
