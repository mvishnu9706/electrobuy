package com.electrobuy.backend.controller;

import com.electrobuy.backend.entity.Role;
import com.electrobuy.backend.entity.User;
import com.electrobuy.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor // Automatically generates high-performance immutable constructor injection
public class UserController {

    // Injecting the Service layer rather than direct Repository mutations to
    // enforce password encryption
    private final UserService userService;

    /**
     * Endpoint to register a new system customer with active security password
     * encryption.
     * URL: POST http://localhost:8085/api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if (user.getRole() == null) {
                user.setRole(Role.CUSTOMER);
            }
            // Routed through your UserService layer to securely trigger BCrypt and
            // collision validations
            User savedUser = userService.registerUser(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint to fetch an active user profile using their unique primary
     * identification key.
     * URL: GET http://localhost:8085/api/users/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}