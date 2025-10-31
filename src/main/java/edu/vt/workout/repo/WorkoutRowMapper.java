package edu.vt.workout.repo;

import edu.vt.workout.model.Workout;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

//--
// this mapper converts a row from a sql resultset into a workout java object.
// 
// used by: workoutcontroller
//--

public class WorkoutRowMapper implements RowMapper<Workout> {
  @Override
  public Workout mapRow(ResultSet rs, int rowNum) throws SQLException {
    // create a new workout instance to store the mapped data
    Workout w = new Workout();
    // map each database column to the corresponding workout field
    w.setId(rs.getObject("id", Long.class)); //workout id
    w.setUserId(rs.getObject("user_id", Long.class)); //assocatied user id

    // exercise name
    String exercise = rs.getString("exercise_name");
    // if exercise_name doesnt exist, default to name
    if (exercise == null) exercise = rs.getString("name");
    w.setExerciseName(exercise);

    //workout fields
    w.setMuscleGroup(rs.getString("muscle_group"));
    w.setSets(rs.getObject("sets", Integer.class));
    w.setReps(rs.getObject("reps", Integer.class));
    w.setWeight(rs.getObject("weight", Double.class));  // nullable
    w.setUnits(rs.getString("units"));
    // convert sql timestamp to localdatetime
    if (rs.getTimestamp("date") != null) w.setDate(rs.getTimestamp("date").toLocalDateTime());
    return w; //return workout object
  }
}
