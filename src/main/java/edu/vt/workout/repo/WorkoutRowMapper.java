package edu.vt.workout.repo;

import edu.vt.workout.model.Workout;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkoutRowMapper implements RowMapper<Workout> {
  @Override
  public Workout mapRow(ResultSet rs, int rowNum) throws SQLException {
    Workout w = new Workout();
    w.setId(rs.getLong("id"));
    w.setUserId(rs.getObject("user_id", Long.class));
    w.setExerciseName(rs.getString("exercise_name"));
    w.setMuscleGroup(rs.getString("muscle_group"));
    w.setSets(rs.getInt("sets"));
    w.setReps(rs.getInt("reps"));
     //nullable field
    w.setWeight(rs.getObject("weight", Double.class));
    if (rs.getTimestamp("date") != null) w.setDate(rs.getTimestamp("date").toLocalDateTime());
    return w;
  }
}
