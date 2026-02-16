package com.example.demo.controller;

import com.example.demo.service.RateLimiterService;
import io.github.bucket4j.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Rate Limit Debug Controller
 *
 * This controller is used only for debugging and monitoring
 * the internal state of rate-limiting buckets.
 *
 * Base URL: /debug
 *
 * Purpose:
 * - Inspect remaining tokens for a specific API and user
 * - Verify rate-limiting configuration
 * - Test Bucket4j behavior during development
 *
 * ⚠️ This controller should NOT be exposed in production.
 */
@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class RateLimitDebugController {

    // Service responsible for resolving and managing rate limit buckets
    private final RateLimiterService rateLimiterService;

    /**
     * Checks remaining tokens in a specific bucket.
     *
     * Endpoint: GET /debug/bucket
     *
     * Example:
     * /debug/bucket?api=/transactions&user=123
     *
     * @param api  API endpoint name (used to resolve bucket configuration)
     * @param user User identifier
     * @return Remaining token count in the bucket
     */
    @GetMapping("/bucket")
    public String checkBucket(
            @RequestParam String api,
            @RequestParam String user) {

        // Resolve bucket for API + user + role
        Bucket bucket =
                rateLimiterService.resolveBucket(api, user, "USER");

        // Probe without consuming token (consume = 0)
        ConsumptionProbe probe =
                bucket.tryConsumeAndReturnRemaining(0);

        return "Remaining Tokens: " +
                probe.getRemainingTokens();
    }
}
