package edu.vt.workout.service;

import edu.vt.workout.model.User;
import edu.vt.workout.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String rawPassword) {
        String hashed = passwordEncoder.encode(rawPassword);
        User u = new User();
        u.setUsername(username);
        u.setPassword(hashed);
        return userRepository.save(u);
    }

    public boolean authenticate(String username, String rawPassword) {
        Optional<User> maybe = userRepository.findByUsername(username);
        if (maybe.isEmpty()) return false;
        User u = maybe.get();
        return passwordEncoder.matches(rawPassword, u.getPassword());
    }
}
