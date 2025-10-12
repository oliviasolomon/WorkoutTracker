package edu.vt.workout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"https://workouttracker-d5wa.onrender.com", "http://localhost:8080"})
public class UserController {

    @Autowired
    private JdbcTemplate jdbc;

    // Signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password required"));
        }

        try {
            // Check if username already exists
            List<Integer> exists = jdbc.queryForList(
                "SELECT id FROM users WHERE username = ?", new Object[]{username}, Integer.class
            );
            if (!exists.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username already taken"));
            }

            // Insert user
            jdbc.update(
                "INSERT INTO users (username, password) VALUES (?, ?)",
                username, password
            );

            // Get the new user ID
            Integer userId = jdbc.queryForObject(
                "SELECT id FROM users WHERE username = ?",
                new Object[]{username}, Integer.class
            );

            return ResponseEntity.status(201).body(Map.of("id", userId, "username", username));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Database error", "detail", e.getMessage()));
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password required"));
        }

        try {
            List<Map<String,Object>> rows = jdbc.queryForList(
                "SELECT id, username, password FROM users WHERE username = ?",
                username
            );

            if (rows.isEmpty() || !rows.get(0).get("password").equals(password)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
            }

            Map<String,Object> user = rows.get(0);
            return ResponseEntity.ok(Map.of(
                "id", user.get("id"),
                "username", user.get("username")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Database error", "detail", e.getMessage()));
        }
    }
}
