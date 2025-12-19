package com.refactorai.controller;

import com.refactorai.dto.AuthResponse;
import com.refactorai.dto.LoginRequest;
import com.refactorai.dto.SignupRequest;
import com.refactorai.entity.User;
import com.refactorai.repository.UserRepository;
import com.refactorai.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            // Check if username exists
            if (userRepository.existsByUsername(request.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, null, "Username already exists"));
            }

            // Check if email exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, null, "Email already exists"));
            }

            // Create new user
            String hashedPassword = passwordEncoder.encode(request.getPassword());
            User user = new User(request.getUsername(), request.getEmail(), hashedPassword);
            userRepository.save(user);

            // Generate token
            String token = jwtUtil.generateToken(user.getUsername());

            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), "User registered successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, null, "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Find user
            Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(null, null, "Invalid username or password"));
            }

            User user = userOpt.get();

            // Check password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(null, null, "Invalid username or password"));
            }

            // Generate token
            String token = jwtUtil.generateToken(user.getUsername());

            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), "Login successful"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, null, "Error: " + e.getMessage()));
        }
    }
}