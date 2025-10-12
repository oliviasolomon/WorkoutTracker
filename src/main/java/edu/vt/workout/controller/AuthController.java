package edu.vt.workout.controller;

import edu.vt.workout.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<User> mapper = (ResultSet rs, int rowNum) -> {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return u;
    };

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        List<User> users = jdbc.query("SELECT * FROM users WHERE username = ?", new Object[]{username}, mapper);

        if (users.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }

        User user = users.get(0);
        if (BCrypt.checkpw(password, user.getPasswordHash())) {
            // For now just return user ID (later can implement JWT/session)
            return ResponseEntity.ok(Map.of("user_id", user.getId(), "username", user.getUsername()));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }
    }

}