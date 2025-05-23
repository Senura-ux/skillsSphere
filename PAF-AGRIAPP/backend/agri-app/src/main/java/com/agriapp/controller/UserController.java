package com.agriapp.controller;

import com.agriapp.model.User;
import com.agriapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Date;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow requests from any origin for development
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        user.setId(id);
        return new ResponseEntity<>(userService.updateUser(user), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/badges/{badge}")
    public ResponseEntity<Void> addBadge(@PathVariable String id, @PathVariable String badge) {
        userService.addBadge(id, badge);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/follow/{followedId}")
    public ResponseEntity<Void> followUser(@PathVariable String id, @PathVariable String followedId) {
        userService.followUser(id, followedId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/unfollow/{unfollowedId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable String id, @PathVariable String unfollowedId) {
        userService.unfollowUser(id, unfollowedId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> credentials) {
        System.out.println("Login attempt: " + credentials);
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        // Validate credentials
        Optional<User> userOptional = userService.getUserByUsername(username);
        if (userOptional.isEmpty() || !userService.validatePassword(password, userOptional.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        
        User user = userOptional.get();
        
        // Generate JWT token
        String token = userService.generateToken(user);
        
        // Create response
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("token", token);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        // Check if username or email already exists
        if (userService.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already taken");
        }
        
        if (userService.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }
        
        // Set default properties for new user
        user.setCreatedAt(new Date());
        user.setRole("user"); // Default role
        
        // Create the user (service should handle password hashing)
        User createdUser = userService.createUser(user);
        
        // Generate JWT token
        String token = userService.generateToken(createdUser);
        
        // Create response with user and token
        Map<String, Object> response = new HashMap<>();
        response.put("user", createdUser);
        response.put("token", token);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        User user = userService.getUserFromToken(token);

        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}