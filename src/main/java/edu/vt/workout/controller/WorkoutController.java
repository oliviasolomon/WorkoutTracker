package edu.vt.workout.controller;

import edu.vt.workout.model.Workout;
import edu.vt.workout.repo.WorkoutRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;

@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = {"https://workouttracker25.netlify.app", "http://localhost:8080"})
public class WorkoutController {

  @Autowired
  private JdbcTemplate jdbc;

  private final WorkoutRowMapper mapper = new WorkoutRowMapper();

  @GetMapping
  public ResponseEntity<?> list(@RequestParam(value="user_id", required=false) Integer userId) {
    if (userId != null) {
      return ResponseEntity.ok(jdbc.query("SELECT * FROM workouts WHERE user_id = ? ORDER BY date DESC",
          new Object[]{userId}, mapper));
    } else {
      return ResponseEntity.ok(jdbc.query("SELECT * FROM workouts ORDER BY date DESC", mapper));
    }
  }

  @GetMapping("{id}")
  public ResponseEntity<Workout> getOne(@PathVariable("id") Long id) {
    var rows = jdbc.query("SELECT * FROM workouts WHERE id = ?", new Object[]{id}, mapper);
    if (rows.isEmpty()) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(rows.get(0));
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody Map<String,Object> body) {
    try {
      // parse and validate inputs carefully
      Integer userId = body.get("user_id") == null ? 1 : ((Number)body.get("user_id")).intValue();
      Object exObj = body.get("exercise");
      if (exObj == null) return ResponseEntity.badRequest().body(Map.of("error","exercise required"));
      String exercise = String.valueOf(exObj).trim();
      if (exercise.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","exercise required"));

      Integer sets = body.get("sets") == null ? null : ((Number)body.get("sets")).intValue();
      Integer reps = body.get("reps") == null ? null : ((Number)body.get("reps")).intValue();
      Double weight = body.get("weight") == null ? null : ((Number)body.get("weight")).doubleValue();

      if (sets == null || reps == null) return ResponseEntity.badRequest().body(Map.of("error","sets and reps required"));
      if (sets < 0 || reps < 0) return ResponseEntity.badRequest().body(Map.of("error","sets/reps must be >= 0"));

      // Lookup muscle_group from exercises table (defensive)
      String muscle = null;
      try {
        muscle = jdbc.queryForObject(
            "SELECT muscle_group FROM exercises WHERE name = ?",
            new Object[]{exercise},
            String.class
        );
      } catch (EmptyResultDataAccessException ex) {
        // exercise not found in exercises table — fallback to NULL (or you could set "Unknown")
        muscle = null;
      }

      // Build INSERT statement (columns and parameter indices must match)
      String sql = "INSERT INTO workouts (user_id, exercise, sets, reps, weight, muscle_group) VALUES (?, ?, ?, ?, ?, ?)";
      KeyHolder kh = new GeneratedKeyHolder();
      jdbc.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, userId);
        ps.setString(2, exercise);
        ps.setInt(3, sets);
        ps.setInt(4, reps);
        if (weight == null) ps.setNull(5, java.sql.Types.DOUBLE); else ps.setDouble(5, weight);
        if (muscle == null) ps.setNull(6, java.sql.Types.VARCHAR); else ps.setString(6, muscle);
        return ps;
      }, kh);

      Number key = kh.getKey();
      Long newId = key != null ? key.longValue() : null;
      if (newId == null) return ResponseEntity.status(500).body(Map.of("error","insert failed"));

      Workout w = jdbc.queryForObject("SELECT * FROM workouts WHERE id = ?", new Object[]{newId}, mapper);
      return ResponseEntity.status(201).body(w);

    } catch (ClassCastException | NullPointerException ex) {
      return ResponseEntity.badRequest().body(Map.of("error","type error", "detail", ex.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error","db error", "detail", e.getMessage()));
    }
  }
}
