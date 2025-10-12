package edu.vt.workout.controller;

import edu.vt.workout.model.User;
import edu.vt.workout.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index() {
        return "index"; // returns index.html from templates
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         Model model) {
        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("error", "Username already exists!");
            return "index";
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userRepository.save(user);
        model.addAttribute("message", "Sign up successful! Please log in.");
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            model.addAttribute("message", "Login successful!");
        } else {
            model.addAttribute("error", "Invalid credentials");
        }
        return "index";
    }
}
