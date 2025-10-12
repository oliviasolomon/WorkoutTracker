package edu.vt.workout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"https://workouttracker-d5wa.onrender.com", "http://localhost:8080"})
public class UserController {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<Map<String, Object>> mapper = (rs, rowNum) -> Map.of(
            "id", rs.getLong("id"),
            "username", rs.getString("username")
    );

    // Signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error","Invalid username/password"));
        }

        // Hash password (simple SHA256, can replace with bcrypt if preferred)
        String passwordHash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password);

        try {
            jdbc.update("INSERT INTO users (username, password_hash) VALUES (?, ?)", username, passwordHash);
            Map<String, Object> user = jdbc.queryForObject("SELECT id, username FROM users WHERE username = ?", new Object[]{username}, mapper);
            return ResponseEntity.status(201).body(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error","Username already exists"));
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Missing username/password"));
        }

        String passwordHash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password);

        try {
            Map<String, Object> user = jdbc.queryForObject(
                    "SELECT id, username FROM users WHERE username = ? AND password_hash = ?",
                    new Object[]{username, passwordHash},
                    mapper
            );
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error","Invalid credentials"));
        }
    }
}
