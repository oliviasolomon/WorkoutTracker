package edu.vt.workout.controller;

import edu.vt.workout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// Serve static index.html from src/main/resources/static/index.html
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    // Simple REST endpoints for signup/login; front-end can POST JSON to these
    @RestController
    @RequestMapping("/api/auth")
    public static class AuthController {
        private final UserService userService;

        @Autowired
        public AuthController(UserService userService) {
            this.userService = userService;
        }

        record AuthRequest(String username, String password) {}

        @PostMapping("/signup")
        public ResponseEntity<String> signup(@RequestBody AuthRequest req) {
            userService.register(req.username(), req.password());
            return ResponseEntity.ok("ok");
        }

        @PostMapping("/login")
        public ResponseEntity<String> login(@RequestBody AuthRequest req) {
            boolean ok = userService.authenticate(req.username(), req.password());
            if (ok) return ResponseEntity.ok("ok");
            return ResponseEntity.status(401).body("invalid");
        }
    }
}
