package edu.vt.workout.repo;

import edu.vt.workout.model.Log;
import edu.vt.workout.model.Workout;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LogRowMapper implements RowMapper<Log> {
  @Override
  public Log mapRow(ResultSet rs, int rowNum) throws SQLException {
    Log l = new Log();
    l.setId(rs.getObject("l_id", Long.class));
    l.setWorkoutId(rs.getObject("l_workout_id", Long.class));
    l.setUserId(rs.getObject("l_user_id", Long.class));
    l.setSets(rs.getObject("sets", Integer.class));
    l.setReps(rs.getObject("reps", Integer.class));
    l.setWeight(rs.getObject("weight", Double.class));
    if (rs.getTimestamp("l_date") != null) l.setDate(rs.getTimestamp("l_date").toLocalDateTime());

    Workout w = new Workout();
    w.setId(rs.getObject("w_id", Long.class));
    w.setUserId(rs.getObject("w_user_id", Long.class));
    w.setExerciseName(rs.getString("w_exercise_name"));
    w.setMuscleGroup(rs.getString("w_muscle_group"));
    l.setWorkout(w);

    return l;
  }
}
