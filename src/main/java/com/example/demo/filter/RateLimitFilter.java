package com.example.demo.filter;

import com.example.demo.service.RateLimiterService;
import io.github.bucket4j.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Global rate limiting filter applied once per request.
 *
 * This filter:
 *  - Identifies the API being accessed.
 *  - Determines the user identity (authenticated user or IP address).
 *  - Resolves the appropriate rate-limit bucket using {@link RateLimiterService}.
 *  - Blocks requests that exceed configured limits.
 *
 * Uses Bucket4j for token-based rate limiting.
 */
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(RateLimitFilter.class);

    /**
     * Service responsible for resolving rate-limit buckets
     * based on API name, user key, and role.
     */
    private final RateLimiterService rateLimiterService;

    /**
     * Core filtering logic executed once per HTTP request.
     *
     * @param request  Incoming HTTP request
     * @param response HTTP response
     * @param filterChain Filter chain for forwarding request
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String apiName = extractApiName(path);

        // If the endpoint is not rate-limited, continue normally
        if (apiName == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String userKey;
        String role = "USER";

        // Determine whether the request is authenticated
        if (auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {

            userKey = auth.getName();

            role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("USER");

        } else {
            // Fallback to client IP if unauthenticated
            userKey = request.getRemoteAddr();
            role = "USER";
        }

        // Resolve rate limit bucket
        Bucket bucket =
                rateLimiterService.resolveBucket(apiName, userKey, role);

        // Attempt token consumption
        ConsumptionProbe probe =
                bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {

            // Add remaining token information in header
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));

            filterChain.doFilter(request, response);

        } else {

            // Calculate retry wait time
            long waitSeconds =
                    probe.getNanosToWaitForRefill() / 1_000_000_000;

            log.warn("Rate limit exceeded for userKey: {}", userKey);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.addHeader("X-Rate-Limit-Retry-After",
                    String.valueOf(waitSeconds));

            response.getWriter().write("""
            {
              "status": 429,
              "error": "Too Many Requests",
              "message": "Rate limit exceeded",
              "retryAfterSeconds": %d
            }
            """.formatted(waitSeconds));
        }
    }

    /**
     * Extracts a logical API name from the request path.
     * This is used to map requests to configured rate-limit rules.
     *
     * @param path Request URI
     * @return API name or null if no rate limiting is required
     */
    private String extractApiName(String path) {

        if (path.startsWith("/transactions")) return "transaction";
        if (path.startsWith("/products")) return "product";
        if (path.startsWith("/sales")) return "sales";
        if (path.startsWith("/refund")) return "refund";
        if (path.startsWith("/auth")) return "auth";

        return null;
    }
}
