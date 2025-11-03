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

//--
// this controller uses the jdbctemplate for direct sql queries and
// communicates with workoutrowmapper to map resultset rows to
// workout model instances.
//--

@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = {"https://workouttracker-d5wa.onrender.com"})
public class WorkoutController {

    private final JdbcTemplate jdbc;
    private final WorkoutRowMapper mapper = new WorkoutRowMapper();

    @Autowired
    public WorkoutController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // returns all workouts (optional filter by user_id parameter)
    @GetMapping
    public List<Workout> list(@RequestParam(value = "user_id", required = false) Integer userId) {
        if (userId != null) {
            // parameterized query to prevent sql injection
            return jdbc.query("SELECT * FROM workouts WHERE user_id = ? ORDER BY date DESC",
                    new Object[]{userId}, mapper);
        } else {
            // return all workouts ordered by most recent date
            return jdbc.query("SELECT * FROM workouts ORDER BY date DESC", mapper);
        }
    }

    //returns a single workout by id
    @GetMapping("{id}")
    public ResponseEntity<Workout> getOne(@PathVariable("id") Long id) {
        List<Workout> rows = jdbc.query("SELECT * FROM workouts WHERE id = ?", new Object[]{id}, mapper);
        if (rows.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(rows.get(0));
    }

    // creates a new workout template; expected json: 
    // user_id, exercise, sets (defaults to 0), reps (defaults to 0), weight (nullable)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String,Object> body) {
        try {
            //reads inputs with conversions and defaults
            final Integer userId = body.get("user_id") == null ? 1 : ((Number)body.get("user_id")).intValue();
            final String exercise = body.get("exercise") == null ? "" : String.valueOf(body.get("exercise")).trim();
            final Integer sets = body.get("sets") == null ? 0 : ((Number)body.get("sets")).intValue();
            final Integer reps = body.get("reps") == null ? 0 : ((Number)body.get("reps")).intValue();
            final Double weight = body.get("weight") == null ? null : ((Number)body.get("weight")).doubleValue();
            final Boolean favorite = body.get("favorite") == null ? Boolean.FALSE : (Boolean) body.get("favorite");
            // validation: exercise field must be filled, sets, reps, and weight must be positive
            if (exercise.isEmpty() || sets < 0 || reps < 0 || weight < 0) {
                return ResponseEntity.badRequest().body(Map.of("error","invalid input"));
            }

            // determine muscle group from exercises table
            String muscleTemp = null;
            try {
                muscleTemp = jdbc.queryForObject(
                    "SELECT muscle_group FROM exercises WHERE name = ?",
                    new Object[]{exercise},
                    String.class
                );
            } catch (Exception ignored) { }
            final String muscle = muscleTemp; // make effectively final for lambda below

            //insert workout and capture generated key
            final String sql = "INSERT INTO workouts (user_id, exercise_name, sets, reps, weight, muscle_group, favorite) VALUES (?, ?, ?, ?, ?, ?,?)";
            KeyHolder kh = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, userId);        // user owner
                ps.setString(2, exercise);   // exercise name
                ps.setInt(3, sets);          // default sets
                ps.setInt(4, reps);          // default reps
                if (weight == null) ps.setNull(5, java.sql.Types.DOUBLE); else ps.setDouble(5, weight); //weight
                if (muscle == null) ps.setNull(6, java.sql.Types.VARCHAR); else ps.setString(6, muscle); //muscle group
                ps.setBoolean(7, favorite); //favorite flag
                return ps;
            }, kh);

            // get generated id and return inserted row
            Number key = kh.getKey();
            Long newId = key != null ? key.longValue() : null;
            if (newId == null) return ResponseEntity.status(500).body(Map.of("error","insert failed"));

            Workout w = jdbc.queryForObject("SELECT * FROM workouts WHERE id = ?", new Object[]{newId}, mapper);
            return ResponseEntity.status(201).body(w);

        } catch (Exception e) {
            // return errors for debugging
            return ResponseEntity.status(500).body(Map.of("error","db error", "detail", e.getMessage()));
        }
    }
}
