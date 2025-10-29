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
// this controller provides rest api for creating and retrieving workout log entries.
// a "log" is a recorded instance of a workout (user, exercises, sets, reps, weight, date).
// post /api/logs accepts a json body with workout_id, user_id, sets, reps, weight, and optional iso-8601 date (date defaults to now if omitted).
//
// returns a list of logs with nested workout metadata via join because "logrowmapper" expects aliased columns.
// returns single log by id with nested workout metadata.
// creates new log rows and returns the created row with nested workout metadata.
//
// "logrowmapper" expects column aliases prefixed with "l_" for log fields and "w_" for workout fields.
//--

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = {"https://workouttracker-d5wa.onrender.com", "http://localhost:8080", "http://localhost:3000"})
public class LogController {
  private final JdbcTemplate jdbc;
  private final LogRowMapper mapper = new LogRowMapper();

  @Autowired
  public LogController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  // returns a list of log objects.
  // if user_id is provided as a query parameter, only logs for that user are returned.
  // dach log includes a nested workout object using join for column aliases expected by "logrowmapper".
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

  // returns single log by id with nested workout
  // returns 404 if not found
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

  // creates a new log entry expects:
  // workout_id, user_id (optional: defaults to 1), sets, reps, weight (null allowed), date (optional iso-8601, defaults to now)
  // returns 201 on success
  // returns 400 for bad input or 500 for database errors
  @PostMapping
  public ResponseEntity<?> create(@RequestBody Map<String,Object> body) {
    try {
      // extract and convert json body fields to java types
      // defaults to null value if field is missing
      final Long workoutId = body.get("workout_id") == null ? null : ((Number)body.get("workout_id")).longValue();
      final Integer userId = body.get("user_id") == null ? 1 : ((Number)body.get("user_id")).intValue();
      final Integer sets = body.get("sets") == null ? null : ((Number)body.get("sets")).intValue();
      final Integer reps = body.get("reps") == null ? null : ((Number)body.get("reps")).intValue();
      final Double weight = body.get("weight") == null ? null : ((Number)body.get("weight")).doubleValue();
      // no provided date defaults to currrent timestamp, otherwise parse iso-8601 string
      final LocalDateTime date = body.get("date") == null ? LocalDateTime.now()
              : LocalDateTime.parse(String.valueOf(body.get("date")));

      // validate workout_id (mandatory to link log to workout)
      if (workoutId == null) return ResponseEntity.badRequest().body(Map.of("error","workout_id required"));

      // sql insert statement for new log row
      final String sql = "INSERT INTO logs (workout_id, user_id, sets, reps, weight, date) VALUES (?, ?, ?, ?, ?, ?)";
      // captures auto-generated primary key of inserted log
      KeyHolder kh = new GeneratedKeyHolder();
      // execute the sql insert using jdbctemplate
      jdbc.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, workoutId); // assign workout foreign key
        ps.setInt(2, userId);     // assign user foreign key
        // handle numeric fields; if null, explicitly set sql null
        if (sets == null) ps.setNull(3, java.sql.Types.INTEGER); else ps.setInt(3, sets);
        if (reps == null) ps.setNull(4, java.sql.Types.INTEGER); else ps.setInt(4, reps);
        if (weight == null) ps.setNull(5, java.sql.Types.DOUBLE); else ps.setDouble(5, weight);
        ps.setTimestamp(6, Timestamp.valueOf(date)); // insert timestamp
        return ps;
      }, kh);

      // retrieve log id from database
      Number key = kh.getKey();
      Long newId = key != null ? key.longValue() : null;
      // if no id was generated, error occurred
      if (newId == null) return ResponseEntity.status(500).body(Map.of("error","insert failed"));

      // return the created log with joined workout metadata
      // join pulls exercise_name and muscle_group from workouts
      String selectSql =
        "SELECT l.id AS l_id, l.workout_id AS l_workout_id, l.user_id AS l_user_id, l.sets, l.reps, l.weight, l.date AS l_date, " +
        "w.id AS w_id, w.user_id AS w_user_id, w.exercise_name AS w_exercise_name, w.muscle_group AS w_muscle_group " +
        "FROM logs l JOIN workouts w ON l.workout_id = w.id WHERE l.id = ?";

      // query returns single log object mapped by "logrowmapper"
      Log l = jdbc.queryForObject(selectSql, new Object[]{newId}, mapper);
      // return http 201 created with new log object as json
      return ResponseEntity.status(201).body(l);

    } catch (Exception e) {
      // if any unexpected error occurs respond with 500 internal server error
      return ResponseEntity.status(500).body(Map.of("error","db error", "detail", e.getMessage()));
    }
  }
}
