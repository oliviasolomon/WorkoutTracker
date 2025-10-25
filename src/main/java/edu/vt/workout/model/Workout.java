package edu.vt.workout.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class Workout {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "user_id", nullable = false)
  private long userId;
  @Column(name = "exercise_name", unique = true, nullable = false)
  private String exerciseName;
  @Column(name = "muscle_group", nullable = false)
  private String muscleGroup;
  @Column(nullable = false)
  private String[] units;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public long getUserId() {return userId; }
  public void setUserId(long userId) { this.userId = userId; }
  
  public String getExerciseName() { return exerciseName; }
  public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

  public String getMuscleGroup() { return muscleGroup; }
  public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }
  
  public String[] getUnits() { return units; }
  public void setUnits(String[] units) { this.units = units; }
}
