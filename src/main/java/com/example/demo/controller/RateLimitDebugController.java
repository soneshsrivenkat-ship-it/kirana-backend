package com.example.demo.controller;

import com.example.demo.service.RateLimiterService;
import io.github.bucket4j.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class RateLimitDebugController {

    private final RateLimiterService rateLimiterService;

    @GetMapping("/bucket")
    public String checkBucket(
            @RequestParam String api,
            @RequestParam String user) {

        Bucket bucket =
                rateLimiterService.resolveBucket(api, user, "USER");

        ConsumptionProbe probe =
                bucket.tryConsumeAndReturnRemaining(0);

        return "Remaining Tokens: " +
                probe.getRemainingTokens();
    }
}
