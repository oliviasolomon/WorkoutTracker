package edu.vt.workout.controller;

import edu.vt.workout.model.Log;
import edu.vt.workout.repo.LogRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = {"https://workouttracker-d5wa.onrender.com", "http://localhost:8080", "http://localhost:3000"})
public class LogController {
  private final JdbcTemplate jdbc;
  private final LogRowMapper mapper = new LogRowMapper();

  @Autowired
  public LogController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  // list with nested workout metadata using JOIN and column aliases expected by mapper
  @GetMapping
  public List<Log> list(@RequestParam(value = "user_id", required = false) Integer userId) {
    String sql =
      "SELECT l.id AS l_id, l.workout_id AS l_workout_id, l.user_id AS l_user_id, l.sets, l.reps, l.weight, l.date AS l_date, " +
      "w.id AS w_id, w.user_id AS w_user_id, w.exercise_name AS w_exercise_name, w.muscle_group AS w_muscle_group " +
      "FROM logs l JOIN workouts w ON l.workout_id = w.id ";
    if (userId != null) {
      sql += "WHERE l.user_id = ? ORDER BY l.date DESC";
      return jdbc.query(sql, new Object[]{userId}, mapper);
    } else {
      sql += "ORDER BY l.date DESC";
      return jdbc.query(sql, mapper);
    }
  }

  @GetMapping("{id}")
  public ResponseEntity<Log> getOne(@PathVariable("id") Long id) {
    String sql =
      "SELECT l.id AS l_id, l.workout_id AS l_workout_id, l.user_id AS l_user_id, l.sets, l.reps, l.weight, l.date AS l_date, " +
      "w.id AS w_id, w.user_id AS w_user_id, w.exercise_name AS w_exercise_name, w.muscle_group AS w_muscle_group " +
      "FROM logs l JOIN workouts w ON l.workout_id = w.id WHERE l.id = ?";
    List<Log> rows = jdbc.query(sql, new Object[]{id}, mapper);
    if (rows.isEmpty()) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(rows.get(0));
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody Map<String,Object> body) {
    try {
      final Long workoutId = body.get("workout_id") == null ? null : ((Number)body.get("workout_id")).longValue();
      final Integer userId = body.get("user_id") == null ? 1 : ((Number)body.get("user_id")).intValue();
      final Integer sets = body.get("sets") == null ? null : ((Number)body.get("sets")).intValue();
      final Integer reps = body.get("reps") == null ? null : ((Number)body.get("reps")).intValue();
      final Double weight = body.get("weight") == null ? null : ((Number)body.get("weight")).doubleValue();
      final LocalDateTime date = body.get("date") == null ? LocalDateTime.now()
              : LocalDateTime.parse(String.valueOf(body.get("date")));

      if (workoutId == null) return ResponseEntity.badRequest().body(Map.of("error","workout_id required"));

      final String sql = "INSERT INTO logs (workout_id, user_id, sets, reps, weight, date) VALUES (?, ?, ?, ?, ?, ?)";
      KeyHolder kh = new GeneratedKeyHolder();
      jdbc.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, workoutId);
        ps.setInt(2, userId);
        if (sets == null) ps.setNull(3, java.sql.Types.INTEGER); else ps.setInt(3, sets);
        if (reps == null) ps.setNull(4, java.sql.Types.INTEGER); else ps.setInt(4, reps);
        if (weight == null) ps.setNull(5, java.sql.Types.DOUBLE); else ps.setDouble(5, weight);
        ps.setTimestamp(6, Timestamp.valueOf(date));
        return ps;
      }, kh);

      Number key = kh.getKey();
      Long newId = key != null ? key.longValue() : null;
      if (newId == null) return ResponseEntity.status(500).body(Map.of("error","insert failed"));

      // return the created Log with nested Workout
      String selectSql =
        "SELECT l.id AS l_id, l.workout_id AS l_workout_id, l.user_id AS l_user_id, l.sets, l.reps, l.weight, l.date AS l_date, " +
        "w.id AS w_id, w.user_id AS w_user_id, w.exercise_name AS w_exercise_name, w.muscle_group AS w_muscle_group " +
        "FROM logs l JOIN workouts w ON l.workout_id = w.id WHERE l.id = ?";
      Log l = jdbc.queryForObject(selectSql, new Object[]{newId}, mapper);
      return ResponseEntity.status(201).body(l);

    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error","db error", "detail", e.getMessage()));
    }
  }
}
