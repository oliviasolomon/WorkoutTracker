package edu.vt.workout.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

//--
// this model represents a workout entry created by a user, with each
// record corresponding to one exercise performed in a workout session,
// and maps to the "workouts" table in the database.
//
// used by: workoutcontroller, logcontroller, workoutrowmapper
//--

@Entity
@Table(name = "workouts")
public class Workout {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment primary key
  private Long id;

  @Column(name = "user_id", nullable = false) //foreign key reference to user
  private Long userId;

  @Column(name = "exercise_name", nullable = false) //exercise name (required)
  private String exerciseName;

  @Column(name = "muscle_group") //muscle group (optional)
  private String muscleGroup;

  @Column(name = "date") // timestamp of workout
  private LocalDateTime date; 

  @Column(name = "sets") //number of sets
  private Integer sets;

  @Column(name = "reps") //number of reps per set
  private Integer reps;

  @Column(name = "weight") //amount of weight lifted
  private Double weight;

  @Column(name = "units") // units (lbs/kg)
  private String units;

  //getters and setters
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
