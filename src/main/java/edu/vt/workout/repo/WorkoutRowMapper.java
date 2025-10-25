package edu.vt.workout.repo;

import edu.vt.workout.model.Workout;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkoutRowMapper implements RowMapper<Log> {
  @Override
  public Workout mapRow(ResultSet rs, int rowNum) throws SQLException {
    // I'd like to check if the workout has already been created in the 
    // repository, but idk how
    Workout w = new Workout();
    w.setId(rs.getLong("id"));
    w/setUserId(rs.getLong("user_id"));
    // column name used here must match the DB schema: exercise_name
    w.setExerciseName(rs.getString("exercise_name"));
    w.setMuscleGroup(rs.getString("muscle_group"));
    integer sets = rs.getInt("sets");
    integer reps = rs.getInt("reps");
    double weight = rs.getDouble("weight");
    w.setSets(sets);
    w.serReps(reps);
    w.setweight(weight);
    if (rs.getTimestamp("date") != null) w.setDate(rs.getTimestamp("date").toLocalDateTime());
    return w;
  }
}
