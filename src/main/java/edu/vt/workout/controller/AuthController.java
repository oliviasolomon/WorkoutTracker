package edu.vt.workout.controller;

import edu.vt.workout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//--
// this controller handles user authentication and registration requests.
// it exposes rest api endpoints under "/api/auth" for signup and login to create accounts and verify credentials.
// the controller communicates with the "user service" class to handle database operations and credential validation.
// the endpoints return json/text responses for frontend integration for authentication success or failure.
//--

// JSON API for signup/login
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    // record for parsing json request bodies
    public record AuthRequest(String username, String password) {}

    // registers a new user with the provided username and password
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody AuthRequest req) {
        if (req == null || req.username() == null || req.password() == null) {
            // if request is missing fields returns "missing"
            return ResponseEntity.badRequest().body("missing");
        }
        userService.register(req.username(), req.password());
        // on success returns "registered"
        return ResponseEntity.ok("registered");
    }

    // authenticates an existing user with user and password
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest req) {
        if (req == null || req.username() == null || req.password() == null) {
            // if request is missing fields returns "missing"
            return ResponseEntity.badRequest().body("missing");
        }
        boolean ok = userService.authenticate(req.username(), req.password());
        // if credentials are valid returns "ok"
        if (ok) return ResponseEntity.ok("ok");
        // otherwise returns "invalid"
        return ResponseEntity.status(401).body("invalid");
    }
    
    // returns pong if server is alive (for debugging and confirming frontend/backend communcation)
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
