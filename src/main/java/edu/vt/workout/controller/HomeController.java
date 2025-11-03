package edu.vt.workout.controller;

import edu.vt.workout.model.Log;
import edu.vt.workout.model.LogGraph;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.TimeZone;

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

    //maps "/signup" to the signup page
    @GetMapping("/signup") 
    public String signupPage() {
        return "signup.html";
    }

    //maps "/login" to the login page
    @GetMapping("/login")
    public String loginPage() {
        return "login.html";

    //maps "/metrics" to the metrics page
    @GetMapping("/metrics")
    public String metricsPage() {
        return "metrics.html";
    }
}
