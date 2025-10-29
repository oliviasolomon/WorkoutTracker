package edu.vt.workout.controller;

import edu.vt.workout.model.User;
import edu.vt.workout.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//--
// this controller handles the signup and login requests by commincating 
// with the userrepository to interact with the users database table.
//--

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepo;
    // userrepository dependency
    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // registers a new user in the database
    // expects JSON { "username": "...", "password": "..." }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        // extract user and password from request body
        String username = body.get("username");
        String password = body.get("password");

        // validate required fields
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("username and password required");
        }
        // check if user already exists
        if (userRepo.findByUsername(username).isPresent()) {
            return ResponseEntity.status(409).body("username already exists");
        }
        // create and save new user record
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userRepo.save(user);

        return ResponseEntity.ok("user created");
    }

    // authenticates a user based user and password
    // expects JSON { "username": "...", "password": "..." }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        // extract user and password from request body
        String username = body.get("username");
        String password = body.get("password");

        // validates input
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("username and password required");
        }

        // look up user in database and validate credentials
        return userRepo.findByUsername(username)
                .map(u -> {
                    if (u.getPassword().equals(password)) {
                        return ResponseEntity.ok("login success");
                    } else {
                        return ResponseEntity.status(401).body("invalid credentials");
                    }
                })
                // if user not found, return invalid credentials
                .orElseGet(() -> ResponseEntity.status(401).body("invalid credentials"));
    }
}
