package edu.vt.workout.service;

import com.example.workouttracker.model.User;
import com.example.workouttracker.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String email, String password) throws Exception {
        if (userRepository.existsByUsername(username)) {
            throw new Exception("Username already taken");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        return userRepository.save(user);
    }
}
