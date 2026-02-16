package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.demo.filter.RateLimitFilter;

/**
 * Spring Security Configuration.
 *
 * Configures:
 *  - Stateless authentication (JWT based)
 *  - Endpoint authorization rules
 *  - Password encoding
 *  - Security filter chain
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final Logger log =
            LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitFilter rateLimitFilter;

    /**
     * BCrypt password encoder bean.
     * Used for securely hashing user passwords.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        log.info("Initializing BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    /**
     * Main security filter chain configuration.
     *
     * Defines:
     *  - CSRF configuration
     *  - Session management policy
     *  - Role-based endpoint access
     *  - Custom filters ordering
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("Configuring Spring Security Filter Chain");

        http

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> {
                    log.info("Setting session management to STATELESS");
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })

                .authorizeHttpRequests(auth -> {

                    log.info("Configuring endpoint authorization rules");

                    auth

                            // Public endpoints
                            .requestMatchers(
                                    "/auth/register",
                                    "/auth/login",
                                    "/auth/refresh"
                            ).permitAll()

                            // Admin only
                            .requestMatchers("/products/**")
                            .hasRole("ADMIN")

                            // Admin + Staff
                            .requestMatchers("/transactions/**")
                            .hasAnyRole("ADMIN", "STAFF")

                            .requestMatchers("/refund/**")
                            .hasAnyRole("ADMIN", "STAFF")

                            .anyRequest()
                            .authenticated();
                })

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .addFilterAfter(
                        rateLimitFilter,
                        JwtAuthenticationFilter.class
                );

        log.info("Security configuration completed successfully");

        return http.build();
    }
}
