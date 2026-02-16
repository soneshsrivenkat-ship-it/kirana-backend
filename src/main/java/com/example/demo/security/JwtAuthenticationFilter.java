package com.example.demo.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT Authentication Filter.
 *
 * This filter:
 *  - Intercepts every incoming HTTP request
 *  - Extracts the JWT token from the Authorization header
 *  - Validates the token
 *  - Extracts user identity and role
 *  - Sets authentication in Spring Security context
 *
 * Enables stateless authentication using JWT.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        log.debug("Processing JWT authentication for path: {}", path);


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Bearer token found for path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);


        if (!jwtUtil.validateToken(token)) {
            log.warn("Invalid JWT token received for path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtUtil.isRefreshToken(token)) {
            log.warn("Refresh token used as access token for path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }


        String userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);

        log.info("Authenticated request for userId: {} with role: {}", userId, role);

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + role);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(authority)
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
