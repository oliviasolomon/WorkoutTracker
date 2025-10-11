package edu.vt.workout.controller;

import edu.vt.workout.model.Workout;
import edu.vt.workout.repo.WorkoutRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "https://workouttracker25.netlify.app")
public class WorkoutController {

  @Autowired
  private JdbcTemplate jdbc;

  private final WorkoutRowMapper mapper = new WorkoutRowMapper();

  @GetMapping
  public List<Workout> list(@RequestParam(value="user_id", required=false) Integer userId) {
    if (userId != null) {
      return jdbc.query("SELECT * FROM workouts WHERE user_id = ? ORDER BY date DESC", new Object[]{userId}, mapper);
    } else {
      return jdbc.query("SELECT * FROM workouts ORDER BY date DESC", mapper);
    }
  }

  @GetMapping("{id}")
  public ResponseEntity<Workout> getOne(@PathVariable("id") Long id) {
    List<Workout> rows = jdbc.query("SELECT * FROM workouts WHERE id = ?", new Object[]{id}, mapper);
    if (rows.isEmpty()) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(rows.get(0));
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody Map<String,Object> body) {
    try {
      Integer userId = body.get("user_id") == null ? 1 : ((Number)body.get("user_id")).intValue();
      String exercise = String.valueOf(body.get("exercise_name"));
      Integer sets = ((Number)body.get("sets")).intValue();
      Integer reps = ((Number)body.get("reps")).intValue();
      String muscle = body.get("muscle_group") == null ? null : String.valueOf(body.get("muscle_group"));
      Double weight = body.get("weight") == null ? null : ((Number)body.get("weight")).doubleValue();

      if (exercise == null || exercise.trim().isEmpty() || sets < 0 || reps < 0)
        return ResponseEntity.badRequest().body(Map.of("error","invalid input"));

      String sql = "INSERT INTO workouts (user_id, exercise_name, muscle_group, sets, reps, weight) VALUES (?, ?, ?, ?, ?, ?)";
      KeyHolder kh = new GeneratedKeyHolder();
      jdbc.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, userId);
        ps.setString(2, exercise);
        ps.setString(3, muscle);
        ps.setInt(4, sets);
        ps.setInt(5, reps);
        if (weight == null) ps.setNull(6, java.sql.Types.DOUBLE); else ps.setDouble(6, weight);
        return ps;
      }, kh);

      Number key = kh.getKey();
      Long newId = key != null ? key.longValue() : null;
      if (newId == null) return ResponseEntity.status(500).body(Map.of("error","insert failed"));
      Workout w = jdbc.queryForObject("SELECT * FROM workouts WHERE id = ?", new Object[]{newId}, mapper);
      return ResponseEntity.status(201).body(w);

    } catch (ClassCastException | NullPointerException ex) {
      return ResponseEntity.badRequest().body(Map.of("error","type error"));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error","db error", "detail", e.getMessage()));
    }
  }
}
