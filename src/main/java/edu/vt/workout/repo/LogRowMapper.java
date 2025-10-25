package edu.vt.workout.repo;

import edu.vt.workout.model.Log;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LogRowMapper implements RowMapper<Log> {
  @Override
  public Log mapRow(ResultSet rs, int rowNum) throws SQLException {
    Log l = new Log();
    l.setId(rs.getLong("id"));
    l.setWorkoutId(rs.getLong("workout_id"));
    l.setUserId(rs.getLong("user_id"));
    Integer sets = rs.getInt("sets");
    Integer reps = rs.getInt("reps");
    Double weight = rs.getDouble("weight");
    l.setSets(sets);
    l.setReps(reps);
    l.setWeight(weight);
    if (rs.getTimestamp("date") != null) l.setDate(rs.getTimestamp("date").toLocalDateTime());
    return l;
  }
}
