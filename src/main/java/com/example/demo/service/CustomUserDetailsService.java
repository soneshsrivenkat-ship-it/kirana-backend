package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    /**
     * Locates the user based on the provided username.
     *
     * This method is used by Spring Security during authentication.
     * It retrieves the user details from the database and converts
     * them into a {@link org.springframework.security.core.userdetails.UserDetails}
     * object required by the security framework.
     *
     * If the user is not found, a {@link org.springframework.security.core.userdetails.UsernameNotFoundException}
     * is thrown.
     *
     * @param username the username identifying the user whose data is required
     * @return a fully populated UserDetails object (never null)
     * @throws UsernameNotFoundException if the user could not be found
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findById(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(() ->
                        "ROLE_" + user.getRole())
        );
    }
}
