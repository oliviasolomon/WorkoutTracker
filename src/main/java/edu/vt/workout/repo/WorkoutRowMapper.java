package edu.vt.workout.repo;

import edu.vt.workout.model.Workout;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkoutRowMapper implements RowMapper<Workout> {
  @Override
  public Workout mapRow(ResultSet rs, int rowNum) throws SQLException {
    Workout w = new Workout();
    w.setId(rs.getObject("id", Long.class));
    w.setUserId(rs.getObject("user_id", Long.class));

    String exercise = rs.getString("exercise_name");
    if (exercise == null) exercise = rs.getString("name");
    w.setExerciseName(exercise);

    w.setMuscleGroup(rs.getString("muscle_group"));
    w.setSets(rs.getObject("sets", Integer.class));
    w.setReps(rs.getObject("reps", Integer.class));
    w.setWeight(rs.getObject("weight", Double.class));  // nullable
    w.setUnits(rs.getString("units"));
    if (rs.getTimestamp("date") != null) w.setDate(rs.getTimestamp("date").toLocalDateTime());
    return w;
  }
}
