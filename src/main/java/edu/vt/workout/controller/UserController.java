package edu.vt.workout.controller;

import edu.vt.workout.model.User;
import edu.vt.workout.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Signup: expects JSON { "username": "...", "password": "..." }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("username and password required");
        }

        if (userRepo.findByUsername(username).isPresent()) {
            return ResponseEntity.status(409).body("username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // demo only — *hash passwords* in real apps
        userRepo.save(user);

        return ResponseEntity.ok("user created");
    }

    // Login: expects JSON { "username": "...", "password": "..." }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("username and password required");
        }

        return userRepo.findByUsername(username)
                .map(u -> {
                    if (u.getPassword().equals(password)) {
                        return ResponseEntity.ok("login success");
                    } else {
                        return ResponseEntity.status(401).body("invalid credentials");
                    }
                })
                .orElseGet(() -> ResponseEntity.status(401).body("invalid credentials"));
    }
}
