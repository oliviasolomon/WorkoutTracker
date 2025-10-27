package edu.vt.workout.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workouts")
public class Workout {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "exercise_name", nullable = false)
  private String exerciseName;

  @Column(name = "muscle_group")
  private String muscleGroup;

  @Column(name = "date")
  private LocalDateTime date;

  @Column(name = "sets")
  private Integer sets;

  @Column(name = "reps")
  private Integer reps;

  @Column(name = "weight")
  private Double weight;

  @Column(name = "units")
  private String units;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }

  public String getExerciseName() { return exerciseName; }
  public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

  public String getMuscleGroup() { return muscleGroup; }
  public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }

  public LocalDateTime getDate() { return date; }
  public void setDate(LocalDateTime date) { this.date = date; }

  public Integer getSets() { return sets; }
  public void setSets(Integer sets) { this.sets = sets; }

  public Integer getReps() { return reps; }
  public void setReps(Integer reps) { this.reps = reps; }

  public Double getWeight() { return weight; }
  public void setWeight(Double weight) { this.weight = weight; }

  public String getUnits() { return units; }
  public void setUnits(String units) { this.units = units; }
}
