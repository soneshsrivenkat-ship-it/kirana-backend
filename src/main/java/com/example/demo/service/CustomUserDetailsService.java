package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom UserDetailsService implementation.
 *
 * Responsible for loading user details from the database
 * during authentication.
 *
 * Used internally by Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log =
            LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    /**
     * Locates user by username.
     *
     * @param username username provided during login
     * @return UserDetails object required by Spring Security
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        log.debug("Authentication attempt for username: {}", username);

        User user = userRepository.findById(username)
                .orElseThrow(() -> {
                    log.warn("Authentication failed. User not found: {}", username);
                    return new UsernameNotFoundException("User not found");
                });

        log.info("User authenticated successfully: {} with role {}",
                user.getUsername(), user.getRole());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(() ->
                        "ROLE_" + user.getRole())
        );
    }
}
