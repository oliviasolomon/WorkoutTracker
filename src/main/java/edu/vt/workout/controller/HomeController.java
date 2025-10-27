package edu.vt.workout.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// ---
// this controller handles routing for the frontend static html pages directly
// --
@Controller
public class HomeController {

    //maps the root url to the homepage
    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    //maps "/signup "to the signup page
    @GetMapping("/signup") 
    public String signupPage() {
        return "signup.html";
    }

    //maps "/login" to the login page
    @GetMapping("/login")
    public String loginPage() {
        return "login.html";
    }
}
