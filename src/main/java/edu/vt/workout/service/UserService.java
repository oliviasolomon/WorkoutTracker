package edu.vt.workout.service;

import edu.vt.workout.model.User;
import edu.vt.workout.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

//--
// provides core logic for user signup and login.
// handles password hasing and verification.
//--

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //signup
    // returns user entity
    public User register(String username, String rawPassword) {
        String hashed = passwordEncoder.encode(rawPassword); // hash password
        User u = new User();
        u.setUsername(username);
        u.setPassword(hashed);
        return userRepository.save(u); //persist to db
    }

    //login
    // verifies provided credentials match a stored user.
    public boolean authenticate(String username, String rawPassword) {
        Optional<User> maybe = userRepository.findByUsername(username); //find by username
        if (maybe.isEmpty()) return false; // user not found
        User u = maybe.get();
        return passwordEncoder.matches(rawPassword, u.getPassword()); //compare hashes
    }
}
