package edu.vt.workout.repo;

import edu.vt.workout.model.Log;
import edu.vt.workout.model.Workout;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

//--
// this mapper extracts both the log fields and associated workout data and
// to map a single row from an sql query result into a log object.
//
// expected column aliases in sql queries:
// l_id, l_workout_id, l_user_id, sets, reps, weight, l_date
// w_id, w_user_id, w_exercise_name, w_muscle_group
//
// used by: logcontroller
//--

public class LogRowMapper implements RowMapper<Log> {
  @Override
  public Log mapRow(ResultSet rs, int rowNum) throws SQLException {
    // create a log object object to hold dataa
    Log l = new Log();
    l.setId(rs.getObject("l_id", Long.class)); //log id
    l.setWorkoutId(rs.getObject("l_workout_id", Long.class)); // associated workout id
    l.setUserId(rs.getObject("l_user_id", Long.class)); // user
    l.setSets(rs.getObject("sets", Integer.class)); //number of sets
    l.setReps(rs.getObject("reps", Integer.class)); // number of reps
    l.setWeight(rs.getObject("weight", Double.class)); //weight used
    if (rs.getTimestamp("l_date") != null) l.setDate(rs.getTimestamp("l_date").toLocalDateTime()); // convert sql timestamp to localdatetime

    // create embedded workout object nested inside log
    Workout w = new Workout(); // workout id
    w.setId(rs.getObject("w_id", Long.class)); //owner user id
    w.setUserId(rs.getObject("w_user_id", Long.class)); // exercise name
    w.setExerciseName(rs.getString("w_exercise_name")); // muscle group
    w.setMuscleGroup(rs.getString("w_muscle_group")); //attach workout metadata to log
    l.setWorkout(w);

    return l; // return log
  }
}
