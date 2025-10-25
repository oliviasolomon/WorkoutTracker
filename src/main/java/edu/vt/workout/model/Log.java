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
@Table(name = "users")
public class Log
{
    // ~ Fields ................................................................
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // Primary ID of the log
    @Column(name = "workout_id", nullable = false)
    private long workoutID; // ID of the workout specified in this Log
    @Column(name = "user_id", nullable = false)
    private long userID; // ID of the user whom recorded the workout
    @Column(name = "sets", nullable = false)
    private Integer sets; // Number of sets completed
    @Column(name = "reps", nullable = false)
    private Integer reps; // Number of reps completed
    @Column(name = "weight")
    private Double weight; // Weight at which workout was completed
    @Column(name = "date", nullable = false)
    private LocalDateTime date; // Date at which workout was completed

    // ~ Getters & Setters .....................................................
    // ID
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    // WorkoutID
    public long getWorkoutId() { return workoutID; }
    public void setWorkoutId(long workoutID) { this.workoutID = workoutID; }
    // UserID
    public long getUserId() { return userID; }
    public void setUserId(long userID) { this.userID = userID; }
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
}
