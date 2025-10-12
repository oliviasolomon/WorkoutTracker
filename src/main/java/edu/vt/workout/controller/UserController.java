package edu.vt.workout.controller;

import edu.vt.workout.model.User;
import edu.vt.workout.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Health check
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    // Signup: expects JSON { "username":"...", "password":"..." }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "username and password required"));
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("success", false, "message", "username taken"));
        }

        String hash = passwordEncoder.encode(password);
        User u = new User();
        u.setUsername(username);
        u.setPassword(hash);

        try {
            userRepository.save(u);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "database error"));
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "user created"));
    }

    // Login: expects JSON { "username":"...", "password":"..." }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "username and password required"));
        }

        Optional<User> found = userRepository.findByUsername(username);
        if (found.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "invalid credentials"));
        }

        User user = found.get();
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (!matches) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "invalid credentials"));
        }

        // For now we return a simple success message. Replace with JWT/session as needed.
        return ResponseEntity.ok(Map.of("success", true, "message", "login OK"));
    }
}
