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

//--
// REST API for combined logs (formerly separate logs + workouts).
// Endpoints:
// GET    /api/logs          -> list (optional ?user_id=)
// GET    /api/logs/{id}     -> single log
// POST   /api/logs          -> create log (fields: user_id, exercise/exercise_name, sets, reps, weight, units, muscle_group, date optional, favorite optional)
// POST   /api/logs/{id}/favorite -> toggle favorite flag
//--

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = {"https://workouttracker-d5wa.onrender.com", "http://localhost:8080", "http://localhost:3000"})
public class LogController {
  private final JdbcTemplate jdbc;
  private final LogRowMapper mapper = new LogRowMapper();

  @Autowired
  public LogController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  // list logs (optional filter by user_id)
  @GetMapping
  public List<Log> list(@RequestParam(value = "user_id", required = false) Integer userId) {
    if (userId != null) {
      return jdbc.query("SELECT * FROM logs WHERE user_id = ? ORDER BY date DESC", new Object[]{userId}, mapper);
    } else {
      return jdbc.query("SELECT * FROM logs ORDER BY date DESC", mapper);
    }
  }

  // get single log
  @GetMapping("{id}")
  public ResponseEntity<Log> getOne(@PathVariable("id") Long id) {
    List<Log> rows = jdbc.query("SELECT * FROM logs WHERE id = ?", new Object[]{id}, mapper);
    if (rows.isEmpty()) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(rows.get(0));
  }

  // create new log
  @PostMapping
  public ResponseEntity<?> create(@RequestBody Map<String,Object> body) {
    try {
      final Integer userId = body.get("user_id") == null ? 1 : ((Number)body.get("user_id")).intValue();
      final String exercise = body.get("exercise") == null ? (body.get("exercise_name") == null ? "" : String.valueOf(body.get("exercise_name")).trim()) : String.valueOf(body.get("exercise")).trim();
      final Integer sets = body.get("sets") == null ? 0 : ((Number)body.get("sets")).intValue();
      final Integer reps = body.get("reps") == null ? 0 : ((Number)body.get("reps")).intValue();
      final Double weight = body.get("weight") == null ? null : ((Number)body.get("weight")).doubleValue();
      final String units = body.get("units") == null ? "lbs" : String.valueOf(body.get("units"));
      final String muscle = body.get("muscle_group") == null ? null : String.valueOf(body.get("muscle_group"));
      final Boolean favorite = body.get("favorite") == null ? Boolean.FALSE : (Boolean) body.get("favorite");
      final LocalDateTime date = body.get("date") == null ? LocalDateTime.now() : LocalDateTime.parse(String.valueOf(body.get("date")));

      if (exercise.isEmpty() || sets < 0 || reps < 0 || (weight != null && weight < 0)) {
        return ResponseEntity.badRequest().body(Map.of("error","invalid input"));
      }

      final String sql = "INSERT INTO logs (user_id, exercise_name, muscle_group, date, sets, reps, weight, units, favorite) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
      KeyHolder kh = new GeneratedKeyHolder();
      jdbc.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, userId);
        ps.setString(2, exercise);
        if (muscle == null) ps.setNull(3, java.sql.Types.VARCHAR); else ps.setString(3, muscle);
        ps.setTimestamp(4, Timestamp.valueOf(date));
        ps.setInt(5, sets);
        ps.setInt(6, reps);
        if (weight == null) ps.setNull(7, java.sql.Types.DOUBLE); else ps.setDouble(7, weight);
        if (units == null) ps.setNull(8, java.sql.Types.VARCHAR); else ps.setString(8, units);
        ps.setBoolean(9, favorite);
        return ps;
      }, kh);

      Number key = kh.getKey();
      Long newId = key != null ? key.longValue() : null;
      if (newId == null) return ResponseEntity.status(500).body(Map.of("error","insert failed"));
      Log l = jdbc.queryForObject("SELECT * FROM logs WHERE id = ?", new Object[]{newId}, mapper);
      return ResponseEntity.status(201).body(l);

    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error","db error", "detail", e.getMessage()));
    }
  }

  // toggle favorite for a log id (creates endpoint used by frontend)
  @PostMapping("{id}/favorite")
  public ResponseEntity<?> toggleFavorite(@PathVariable("id") Long id) {
    try {
      List<Log> rows = jdbc.query("SELECT * FROM logs WHERE id = ?", new Object[]{id}, mapper);
      if (rows.isEmpty()) return ResponseEntity.notFound().build();
      boolean current = rows.get(0).getFavorite() == Boolean.TRUE;
      jdbc.update("UPDATE logs SET favorite = ? WHERE id = ?", !current, id);
      return ResponseEntity.ok(Map.of("id", id, "favorite", !current));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error","db error", "detail", e.getMessage()));
    }
  }
}
