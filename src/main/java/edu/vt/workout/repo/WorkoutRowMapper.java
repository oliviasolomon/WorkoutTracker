package edu.vt.workout.repo;

import edu.vt.workout.model.Log;
import edu.vt.workout.model.Workout;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class WorkoutRowMapper implements RowMapper<Log> {
  @Override
  public Log mapRow(ResultSet rs, int rowNum) throws SQLException {
    // I'd like to check if the workout has already been created in the 
    // repository, but idk how
      Workout w = new Workout();
    Log l = new Log();
    w.setId(rs.getLong("id"));
    l.setUserId(rs.getInt("user_id"));
    // column name used here must match the DB schema: exercise_name
    w.setExerciseName(rs.getString("exercise_name"));
    w.setMuscleGroup(rs.getString("muscle_group"));
    l.setSets(rs.getInt("sets"));
    l.setReps(rs.getInt("reps"));
    double weight = rs.getDouble("weight");
    if (!rs.wasNull()) l.setWeight(weight);
    l.setDate(rs.getTimestamp("date") != null ? rs.getTimestamp("date").toLocalDateTime() : LocalDateTime.now());
    l.setWorkoutId(w.getId());
    return l;
  }
}
