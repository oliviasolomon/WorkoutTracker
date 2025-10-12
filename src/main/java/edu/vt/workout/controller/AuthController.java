package edu.vt.workout.controller;

import edu.vt.workout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// JSON API for signup/login
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    public record AuthRequest(String username, String password) {}

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody AuthRequest req) {
        if (req == null || req.username() == null || req.password() == null) {
            return ResponseEntity.badRequest().body("missing");
        }
        userService.register(req.username(), req.password());
        return ResponseEntity.ok("registered");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest req) {
        if (req == null || req.username() == null || req.password() == null) {
            return ResponseEntity.badRequest().body("missing");
        }
        boolean ok = userService.authenticate(req.username(), req.password());
        if (ok) return ResponseEntity.ok("ok");
        return ResponseEntity.status(401).body("invalid");
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
