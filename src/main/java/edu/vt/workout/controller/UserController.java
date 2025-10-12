package edu.vt.workout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"https://workouttracker-d5wa.onrender.com", "http://localhost:8080"})
public class UserController {

    private final JdbcTemplate jdbc;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Signup: accepts { username, password } and stores hashed password
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
                "SELECT id FROM users WHERE username = ?",
                new Object[]{username},
                Integer.class
            );
            if (!exists.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username already taken"));
            }

            // Hash password and insert user
            String hash = passwordEncoder.encode(password);
            jdbc.update(
                "INSERT INTO users (username, password_hash) VALUES (?, ?)",
                username, hash
            );

            Integer userId = jdbc.queryForObject(
                "SELECT id FROM users WHERE username = ?",
                new Object[]{username}, Integer.class
            );

            return ResponseEntity.status(201).body(Map.of("id", userId, "username", username));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Database error", "detail", e.getMessage()));
        }
    }

    // Login: accepts { username, password } and verifies against stored hash
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password required"));
        }

        try {
            List<Map<String,Object>> rows = jdbc.queryForList(
                "SELECT id, username, password_hash FROM users WHERE username = ?",
                new Object[]{username}
            );

            if (rows.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
            }

            Map<String,Object> user = rows.get(0);
            String storedHash = (String) user.get("password_hash");
            if (!passwordEncoder.matches(password, storedHash)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
            }

            return ResponseEntity.ok(Map.of(
                "id", user.get("id"),
                "username", user.get("username")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Database error", "detail", e.getMessage()));
        }
    }
}
