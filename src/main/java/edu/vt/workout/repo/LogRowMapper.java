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
    l.setId(rs.getLong("l_id"));
    l.setWorkoutId(rs.getLong("l_workout_id"));
    l.setUserId(rs.getLong("l_user_id"));
    l.setSets(rs.getInt("sets");
    l.setReps(rs.getInt("reps"));
    l.setWeight(rs.getObject("weight", Double.class));
    if (rs.getTimestamp("l_date") != null) l.setDate(rs.getTimestamp("l_date").toLocalDateTime());

    // nested workout from join aliases
    Workout w = new Workout();
    w.setId(rs.getLong("w_id"));
    Long wUser = rs.getObject("w_user_id", Long.class);
    w.setUserId(wUser);
    w.setExerciseName(rs.getString("w_exercise_name"));
    w.setMuscleGroup(rs.getString("w_muscle_group"));
    l.setWorkout(w);

    return l;
  }
}
