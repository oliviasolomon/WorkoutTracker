package edu.vt.workout.model;

import java.time.LocalDateTime;

public class Workout {
  private Long id;
  private Integer userId;
  private String exerciseName;
  private String muscleGroup;
  private Integer sets;
  private Integer reps;
  private Double weight;
  private LocalDateTime date;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Integer getUserId() { return userId; }
  public void setUserId(Integer userId) { this.userId = userId; }

  public String getExerciseName() { return exerciseName; }
  public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

  public String getMuscleGroup() { return muscleGroup; }
  public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }

  public Integer getSets() { return sets; }
  public void setSets(Integer sets) { this.sets = sets; }

  public Integer getReps() { return reps; }
  public void setReps(Integer reps) { this.reps = reps; }

  public Double getWeight() { return weight; }
  public void setWeight(Double weight) { this.weight = weight; }

  public LocalDateTime getDate() { return date; }
  public void setDate(LocalDateTime date) { this.date = date; }
}
