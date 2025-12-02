package edu.vt.workout.repo;

import edu.vt.workout.model.Log;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

//--
// maps a single row from the "logs" table into a Log object.
// expected columns: id, user_id, exercise_name, muscle_group, date, sets, reps, weight, units, favorite
// used by: logcontroller
//--

public class LogRowMapper implements RowMapper<Log> {
  @Override
  public Log mapRow(ResultSet rs, int rowNum) throws SQLException {
    Log l = new Log();
    l.setId(rs.getObject("id", Long.class));
    l.setUserId(rs.getObject("user_id", Long.class));
    l.setExerciseName(rs.getString("exercise_name"));
    l.setMuscleGroup(rs.getString("muscle_group"));
    if (rs.getTimestamp("date") != null) l.setDate(rs.getTimestamp("date").toLocalDateTime());
    l.setSets(rs.getObject("sets", Integer.class));
    l.setReps(rs.getObject("reps", Integer.class));
    l.setWeight(rs.getObject("weight", Double.class));
    l.setUnits(rs.getString("units"));
    Object fav = rs.getObject("favorite");
    if (fav != null) l.setFavorite(rs.getBoolean("favorite"));
    else l.setFavorite(false);
    return l;
  }
}
