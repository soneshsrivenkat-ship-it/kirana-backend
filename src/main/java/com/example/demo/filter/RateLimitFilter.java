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

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(RateLimitFilter.class);

    private final RateLimiterService rateLimiterService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String apiName = extractApiName(path);

        if (apiName == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String userKey;
        String role = "USER";

        if (auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {

            userKey = auth.getName();

            role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("USER");

        } else {

            userKey = request.getRemoteAddr();
            role = "USER";
        }

        Bucket bucket =
                rateLimiterService.resolveBucket(apiName, userKey, role);

        ConsumptionProbe probe =
                bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {

            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));

            filterChain.doFilter(request, response);

        } else {

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

    private String extractApiName(String path) {

        if (path.startsWith("/transactions")) return "transaction";
        if (path.startsWith("/products")) return "product";
        if (path.startsWith("/sales")) return "sales";
        if (path.startsWith("/refund")) return "refund";
        if (path.startsWith("/auth")) return "auth";

        return null;
    }
}
