package edu.vt.workout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@CrossOrigin(origins = "*")
public class ExerciseController {

    private final JdbcTemplate jdbc;

    @Autowired
    public ExerciseController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    public List<String> listExercises() {
        // assumes table "exercises" has a column "name"
        String sql = "SELECT name FROM exercises ORDER BY name ASC";
        return jdbc.queryForList(sql, String.class);
    }
}
