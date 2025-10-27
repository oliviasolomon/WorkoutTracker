package edu.vt.workout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@CrossOrigin(origins = {"https://workouttracker-d5wa.onrender.com"})
public class ExerciseController {

    private final JdbcTemplate jdbc; // helper to directly execute sql queries

    @Autowired // auto injects jbdctemplate from spring's application content
    public ExerciseController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping // handles getters
    public List<String> listExercises() {
        // sql query to fetch all exercise names from exercises table in alphabetical order
        String sql = "SELECT name FROM exercises ORDER BY name ASC";
        // executes query and returns a list of exercise names as strings
        return jdbc.queryForList(sql, String.class);
    }
}
