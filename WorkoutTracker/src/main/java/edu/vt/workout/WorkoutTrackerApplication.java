package edu.vt.workout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//entry point for spring boot workout tracker application
// starts embedded web server

@SpringBootApplication
public class WorkoutTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkoutTrackerApplication.class, args);
    }
}
