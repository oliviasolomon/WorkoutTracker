package edu.vt.workout.repo;

import edu.vt.workout.model.Workout;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class WorkoutRowMapper implements RowMapper<Workout> {
  @Override
  public Workout mapRow(ResultSet rs, int rowNum) throws SQLException {
    Workout w = new Workout();
    w.setId(rs.getLong("id"));
    w.setUserId(rs.getInt("user_id"));
    // column name used here must match the DB schema: exercise_name
    w.setExerciseName(rs.getString("exercise_name"));
    w.setMuscleGroup(rs.getString("muscle_group"));
    w.setSets(rs.getInt("sets"));
    w.setReps(rs.getInt("reps"));
    double weight = rs.getDouble("weight");
    if (!rs.wasNull()) w.setWeight(weight);
    w.setDate(rs.getTimestamp("date") != null ? rs.getTimestamp("date").toLocalDateTime() : LocalDateTime.now());
    return w;
  }
}
