package com.example.demo.service;

import com.example.demo.dao.UserDAO;
import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Registers a new user in the system.
     *
     * This method:
     * 1. Checks if the username already exists.
     * 2. Encrypts the password using BCrypt.
     * 3. Saves the user with the assigned role.
     *
     * @param request contains username, password, and role
     *
     * @throws RuntimeException if the username already exists
     */
    public void register(RegisterRequest request) {

        if (userDAO.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userDAO.save(user);
    }

    /**
     * Authenticates a user and generates JWT tokens.
     *
     * This method:
     * 1. Validates username and password.
     * 2. Generates an access token (short-lived).
     * 3. Generates a refresh token (long-lived).
     *
     * @param request contains username and password
     * @return AuthResponse containing access and refresh tokens
     *
     * @throws RuntimeException if credentials are invalid
     */
    public AuthResponse login(LoginRequest request) {

        User user = userDAO.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid credentials");
        }

        String access = jwtUtil.generateAccessToken(
                user.getId(),
                user.getRole()
        );


        String refresh =
                jwtUtil.generateRefreshToken(
                        user.getUsername());

        return new AuthResponse(access, refresh);
    }

    /**
     * Generates a new access token using a valid refresh token.
     *
     * This method:
     * 1. Validates that the provided token is a refresh token.
     * 2. Extracts the username from the token.
     * 3. Generates a new access token.
     *
     * The original refresh token remains valid until expiration.
     *
     * @param request contains the refresh token
     * @return AuthResponse containing new access token and existing refresh token
     *
     * @throws RuntimeException if the refresh token is invalid
     */
    public AuthResponse refreshToken(
            RefreshTokenRequest request) {

        String token = request.getRefreshToken();

        if (!jwtUtil.isRefreshToken(token)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username =
                jwtUtil.extractUsername(token);

        User user = userDAO.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String newAccess =
                jwtUtil.generateAccessToken(
                        user.getId(),
                        user.getRole());

        return new AuthResponse(newAccess, token);
    }
}
