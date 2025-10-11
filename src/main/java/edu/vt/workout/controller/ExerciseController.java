package edu.vt.workout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@CrossOrigin(origins = {"https://workouttracker25.netlify.app", "http://localhost:8080"})
public class ExerciseController {

    @Autowired
    private JdbcTemplate jdbc;

    /**
     * Returns all predefined exercises for the dropdown.
     */
    @GetMapping
    public List<String> listExercises() {
        String sql = "SELECT name FROM exercises ORDER BY name ASC";
        return jdbc.queryForList(sql, String.class);
    }
}
