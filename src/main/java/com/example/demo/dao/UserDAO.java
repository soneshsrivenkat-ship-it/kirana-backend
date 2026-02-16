package com.example.demo.dao;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * UserDAO
 *
 * Data Access Layer for User entity.
 *
 * Acts as an abstraction between the Service layer
 * and the JPA repository.
 *
 * Responsibilities:
 * - Persisting users
 * - Checking username availability
 * - Fetching user by username (for authentication)
 *
 * Used in:
 * - UserService (registration & login)
 * - Security authentication flow
 */
@Component
@RequiredArgsConstructor
public class UserDAO {

    /**
     * JPA Repository for User entity.
     */
    private final UserRepository userRepository;

    /**
     * Save a new or existing user.
     *
     * Used during:
     * - User registration
     * - Updating user details (if applicable)
     *
     * @param user User entity
     * @return Persisted User
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Check if a username already exists.
     *
     * Used during registration to prevent duplicate users.
     *
     * @param username Username to check
     * @return true if username exists, otherwise false
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Fetch user by username.
     *
     * Used during:
     * - Login authentication
     * - JWT validation
     *
     * @param username Username of the user
     * @return Optional containing User if found
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
