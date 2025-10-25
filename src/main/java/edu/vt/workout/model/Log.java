package edu.vt.workout.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// -------------------------------------------------------------------------
/**
 * This class aims to let the system group together specific workouts without
 * creating a new workout instance every time. This way, workouts can remain
 * relatively static with machines and methods while the growth can be captured
 * via logs.
 * 
 * @author jbrent22
 * @version Oct 20, 2025
 */
@Entity
@Table(name = "logs")
public class Log
{
    // ~ Fields ................................................................
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary ID of the log
    @Column(name = "workout_id", nullable = false)
    private Long workoutID; // ID of the workout specified in this Log
    @Column(name = "user_id", nullable = false)
    private Long userID; // ID of the user whom recorded the workout
    @Column(name = "sets", nullable = false)
    private Integer sets; // Number of sets completed
    @Column(name = "reps", nullable = false)
    private Integer reps; // Number of reps completed
    @Column(name = "weight")
    private Double weight; // Weight at which workout was completed
    @Column(name = "date", nullable = false)
    private LocalDateTime date; // Date at which workout was completed
    @Transcient
    priviate Workout workout; //populated only when joining
    // ~ Getters & Setters .....................................................
    // ID
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // WorkoutID
    public Long getWorkoutId() { return workoutID; }
    public void setWorkoutId(Long workoutID) { this.workoutID = workoutID; }
    // UserID
    public Long getUserId() { return userID; }
    public void setUserId(Long userID) { this.userID = userID; }
    // Sets
    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }
    // Reps
    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }
    // Weight
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    // Date
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public Workout getWorkout() { return workout; }
    public void setWorkout(Workout workout) { this.workout = workout; }
}
