package edu.vt.workout.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Serve static pages
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup.html";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login.html";
    }
}
